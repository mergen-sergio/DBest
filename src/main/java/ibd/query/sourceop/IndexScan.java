/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.sourceop;

import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.lookup.CompositeRowLookupFilter;
import ibd.table.lookup.NoRowLookupFilter;
import ibd.table.lookup.RowLookupFilter;
import ibd.table.lookup.SingleRowColumnLookupLookupFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.Column;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs an index scan over a table
 *
 * @author Sergio
 */
public class IndexScan extends SourceOperation {

    private List<String> columns;

    /**
     * the table reached from this operation
     */
    public Table table;

    /**
     * indicates if there are filter conditions that enable efficient table
     * lookups
     */
    boolean canLookup = false;

    /**
     * the list of conjunctive equality filters conditions that enable efficient
     * table lookups
     */
    List<SingleColumnLookupFilter> fastFilters;

    /**
     * the row to be used as a parameter to the efficient table lookup. It must
     * be filled with values taken from the lookup filters
     */
    LinkedDataRow fastLookupRow;

    /*
    The filter to be used for those columns that cannot be efficiently looked-up
     */
    RowLookupFilter slowLookupFilter;

    /**
     *
     * @param tableAlias the alias of the table reached by this operation
     * @param table the table reached from this operation
     */
    public IndexScan(String tableAlias, Table table) {
        super(tableAlias);
        this.table = table;
        columns = new ArrayList<>();

        for (Column c : table.getPrototype().getColumns()) {
            //if(c.ignore())continue;
            columns.add(c.getName());
        }
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        //separates filters into slow and fast, based on the tables hability to efficiently handle the search condition
        separateFilters();

        //creates the single instance of the lookup row to be used whenever a lookup is required
        fastLookupRow = new LinkedDataRow(table.getPrototype(), true);
    }

    @Override
    public void setDataSourcesInfo() throws Exception {
        super.setDataSourcesInfo();
        dataSources[0].prototype = table.getPrototype();
    }

    //separates filters into slow and fast, based on the tables hability to efficiently handle the search condition
    private void separateFilters() throws Exception {

        //an empty row filter is prepared, just in case
        slowLookupFilter = new NoRowLookupFilter();
        
        //reset variable
        canLookup = false;

        //the parent operator has delegated filters to the index scan
        if (hasDelegatedFilters) {

            //extract the conjunctive equality filters that can be efficiently searched
            LookupFilter joinFilter = parentOperation.getFilters();

            //builds a preliminary list of fast filters
            fastFilters = new ArrayList();
            fillFastFilters(joinFilter);

            //identifies the columns used by the fast filters 
            List<String> columns = new ArrayList();
            for (SingleColumnLookupFilter f : fastFilters) {
                columns.add(f.getColumnDescriptor().getColumnName());
            }

            //checks which of the identified columns are part of the index prefix.
            //If at least one, an efficient lookup is possible
            List<String> pkColumns = table.getPrototype().pKPrefix(columns);
            if (!pkColumns.isEmpty()) {
                canLookup = true;
            }

            //removes from the fast filters list the ones that do not use columns from the pk prefix
            for (int i = fastFilters.size() - 1; i >= 0; i--) {
                String colName = fastFilters.get(i).getColumnDescriptor().getColumnName();
                if (!(pkColumns.contains(colName))) {
                    fastFilters.remove(i);
                }
            }

            //fills the slow Filter with the lookup filters that are not part of the fast Filters list
            slowLookupFilter = fillSlowFilter(parentOperation.getFilters());
        }

    }

    //fills the list of fast filters
    private void fillFastFilters(LookupFilter filter) {
        if (filter instanceof CompositeLookupFilter compositeLookupFilter) {
            if (compositeLookupFilter.getBooleanConnector() == CompositeLookupFilter.OR) {
                return;
            }
            for (LookupFilter f : compositeLookupFilter.getFilters()) {
                fillFastFilters(f);
            }
        } else if (filter instanceof SingleColumnLookupFilter singleColumnLookupFilter) {
            if (singleColumnLookupFilter.getComparisonType() == ComparisonTypes.EQUAL) {
                fastFilters.add(singleColumnLookupFilter);
            }
        }

    }

    //fills the list of slow filters
    private RowLookupFilter fillSlowFilter(LookupFilter filter) throws Exception {

        if (filter instanceof CompositeLookupFilter clf) {

            CompositeRowLookupFilter rowFilter = new CompositeRowLookupFilter(clf.getBooleanConnector());
            for (LookupFilter f : clf.getFilters()) {
                RowLookupFilter rlf = fillSlowFilter(f);
                if (!(rlf instanceof NoRowLookupFilter)) {
                    rowFilter.addFilter(rlf);
                }
            }
            if (rowFilter.getFilters().isEmpty()) {
                return new NoRowLookupFilter();
            }
            return rowFilter;
        } else if (filter instanceof SingleColumnLookupFilter f) {
            if (fastFilters.contains(f)) {
                return new NoRowLookupFilter();
            }
            SingleRowColumnLookupLookupFilter singleRowFilter = new SingleRowColumnLookupLookupFilter(f.getColumnDescriptor().getColumnLocation().colIndex, f.getComparisonType());
            singleRowFilter.setFilter(f);
            return singleRowFilter;
        } else {
            return new NoRowLookupFilter();
        }
    }

    @Override
    public String toString() {
        return "[" + dataSourceAlias + "] Index Scan";
    }

    /**
     * {@inheritDoc }
     *
     * @param lookup
     * @return an iterator that performs an index scan over a table
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new IndexScanIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public boolean exists(List<Tuple> processedTuples, boolean withFilterDelegation) {
        if (canLookup) {
            //sgbd.info.Query.PK_SEARCH++;
            //the lookup row is filled with values from the lookup filters
            for (SingleColumnLookupFilter filter_ : fastFilters) {
                //row.setValue(filter.getColumn(), (Integer) filter.getValue());
                fastLookupRow.setValue(filter_.getColumnDescriptor().getColumnLocation().colIndex, (Integer) filter_.getValue());
            }
            ibd.query.QueryStats.PK_SEARCH++;
            return table.contains(fastLookupRow);
        } else {
            if (slowLookupFilter instanceof NoRowLookupFilter) {
                Iterator iterator;
                try {
                    iterator = table.getAllRecordsIterator();
                    return iterator.hasNext();
                } catch (Exception ex) {
                    //should not return false. An exception should be thrown.
                    return false;
                }

            } else {
                try {
                    Iterator iterator = table.getFilteredRecordsIterator(slowLookupFilter);
                    return iterator.hasNext();
                } catch (Exception ex) {
                    //should not return false. An exception should be thrown.
                    return false;
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();
        map.put(dataSourceAlias, new ArrayList<>(columns));
        return map;
    }

    /**
     * the class that produces resulting tuples from an index scan
     */
    public class IndexScanIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<LinkedDataRow> iterator;

        public IndexScanIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
//          

            //the lookup filters from the parent were already divided into fast and slos filters.
            //now its time to use them
            try {

                //if there exists fast filters
                if (canLookup) {
                    ibd.query.QueryStats.PK_SEARCH++;
                    //the lookup row is filled with values from the fast filters
                    for (SingleColumnLookupFilter filter : fastFilters) {
                        fastLookupRow.setValue(filter.getColumnDescriptor().getColumnLocation().colIndex, filter.getValue());
                    }

                    //the lookup occurs

                    if (slowLookupFilter instanceof NoRowLookupFilter) {
                        //no slow filter needs to be satisfied
                        //iterator = table.getRecords(fastLookupRow).iterator();
                        //iterator = records.iterator();
                        iterator = table.getPKFilteredRecordsIterator(fastLookupRow, slowLookupFilter);
                    } else {
                        //there are slow filters to be satisfied
                        //iterator = table.getRecords(fastLookupRow, slowLookupFilter).iterator;
                        iterator = table.getPKFilteredRecordsIterator(fastLookupRow, slowLookupFilter);
                    }

                    return;

                }

                //if it gets here, no fast filters exist, so we need to scan the entire table
                //In this case, all filters are slow
                if (slowLookupFilter instanceof NoRowLookupFilter) {
                    //if no filter needs to be performed
                    iterator = table.getAllRecordsIterator();
                    //iterator = table.getAllRecords().iterator();
                    return;
                }
                //there exists slow filters to be satisfied
                //the scan has to traverse the entire table and resolve the filters as the rows are accessed
                iterator = table.getFilteredRecordsIterator(slowLookupFilter);
                //iterator = table.getFilteredRecords(slowLookupFilter).iterator();

            } catch (Exception ex) {
            }

        }

        /**
         * {@inheritDoc }
         *
         * @return
         */
        @Override
        protected Tuple findNextTuple() {

            while (iterator.hasNext()) {
                LinkedDataRow row = iterator.next();
                Tuple tuple = new Tuple();
                //the resulting tuple contains a single row taken from the table
                tuple.setSingleSourceRow(dataSources[0].alias, row);
                //no filters need to be applied. They were already processed, wither as fast or slow filters.
                return tuple;

            }

            return null;
        }

    }
}
