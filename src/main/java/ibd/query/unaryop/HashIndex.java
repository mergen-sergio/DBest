/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This unary operation is materialized. It creates a hash containing all the
 * tuples that come from the child operation. Useful for lookups based on
 * equivalence conditions.
 *
 * @author Sergio
 */
public class HashIndex extends UnaryOperation {

    /*
    materialized hash of tuples, using as key the columns that are part of conjunctive equality search conditions.
    It can answer lookups based on equivalence conditions efficiently.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    HashMap<String, List<Tuple>> tuples;

    /**
     * the list of conjunctive equality filters conditions that form keys of the
     * hash
     */
    List<SingleColumnLookupFilter> hashedFilters;

    /**
     * the filters that of conjunctive equality filters conditions that form
     * keys of the hash
     */
    LookupFilter unhashedFilters;
    
    
    boolean memoryUsedDefined = false;
    long memoryUsed = 0;

    /**
     *
     * @param childOperation the child operation
     * @throws Exception
     */
    public HashIndex(Operation childOperation) throws Exception {
        super(childOperation);
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();

        //sets the list of columns that will be part of the hash keys
        prepareHashColumns();

        //erases the previously built hash.
        //a new one is created when the first query is executed. 
        tuples = null;
        memoryUsedDefined = false;
    }

    //sets the list of columns that will be part of the hash keys
    private void prepareHashColumns() throws Exception {

        hashedFilters = new ArrayList();
        unhashedFilters = new NoLookupFilter();
        if (hasDelegatedfilters()) {
            LookupFilter joinFilter = parentOperation.getFilters();

            prepareHashColumns(joinFilter);

            unhashedFilters = copyUnhashedFilters(joinFilter);
        }
    }

    //sets the list of columns that will be part of the hash keys
    private void prepareHashColumns(LookupFilter filter) {
        if (filter instanceof CompositeLookupFilter compositeLookupFilter) {
            if (compositeLookupFilter.getBooleanConnector() == CompositeLookupFilter.OR) {
                return;
            }
            for (LookupFilter f : compositeLookupFilter.getFilters()) {
                prepareHashColumns(f);
            }
        } else if (filter instanceof SingleColumnLookupFilter singleColumnLookupFilter) {
            if (singleColumnLookupFilter.getComparisonType() == ComparisonTypes.EQUAL) {
                hashedFilters.add(singleColumnLookupFilter);
            }
        }

    }

    //Copies the unhashed filters into a new structure
    private LookupFilter copyUnhashedFilters(LookupFilter filter) throws Exception {

        if (filter instanceof CompositeLookupFilter clf) {

            CompositeLookupFilter rowFilter = new CompositeLookupFilter(clf.getBooleanConnector());
            for (LookupFilter f : clf.getFilters()) {
                LookupFilter rlf = copyUnhashedFilters(f);
                if (!(rlf instanceof NoLookupFilter)) {
                    rowFilter.addFilter(rlf);
                }
            }
            if (rowFilter.getFilters().isEmpty()) {
                return new NoLookupFilter();
            }
            return rowFilter;
        } else if (filter instanceof SingleColumnLookupFilter f) {
            if (hashedFilters.contains(f)) {
                return new NoLookupFilter();
            }
            return f;
        } else {
            return new NoLookupFilter();
        }
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new MaterializedIndexIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        return "Hash";
    }

    /**
     * the class that materializes a collection of tuples that come from the
     * child operation using a hash. The query is answered using the hash.
     */
    public class MaterializedIndexIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> it = null;

        public MaterializedIndexIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {

            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            //only the unhashed filters from the parent need to be verififed. The others will be satisfied by the hash search.
            this.lookup = unhashedFilters;
            buildHash();
            queryHash();
        }
        
        private void queryHash(){
        //here is where we build an iterator to traverse the query results
            //the hash is queried using as key the values of the hashed filter columns
            String key = "";
            for (SingleColumnLookupFilter lookupFilter : hashedFilters) {
                key += lookupFilter.getValue().toString();
            }

            List<Tuple> result = tuples.get(key);
            if (result != null) {
                it = result.iterator();
            } else {
                it = new ArrayList<Tuple>().iterator();
            }
        }
        
        private void buildHash(){
        //build hash, if one does not exist yet
            if (tuples == null) {
                tuples = new HashMap();
                memoryUsed = 0;
                try {
                    //accesses and indexes all tuples that come from the child operation
                    it = childOperation.lookUp(processedTuples, false);
                    int tupleSize = childOperation.getTupleSize();
                    while (it.hasNext()) {
                        Tuple tuple = (Tuple) it.next();
                        String key = "";

                        for (SingleColumnLookupFilter lookupFilter : hashedFilters) {
                            ColumnDescriptor col = lookupFilter.getColumnDescriptor();
                            key += tuple.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex).toString();
                        }

                        //String key = tuple.rows[hashColumn.getColumnLocation().rowIndex].getValue(hashColumn.getColumnName()).toString();
                        List tupleList = tuples.get(key);
                        if (tupleList == null) {
                            tupleList = new ArrayList();
                            tuples.put(key, tupleList);
                        }
                        tupleList.add(tuple);
                        memoryUsed+=tupleSize;
                    }

                } catch (Exception ex) {
                }
                
            }

            
        }

        @Override
        protected Tuple findNextTuple() {
            if (!memoryUsedDefined){
                memoryUsedDefined = true;
                QueryStats.MEMORY_USED += memoryUsed;
            }
            while (it.hasNext()) {
                Tuple tp = it.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                //only the unhashed filters need to be checked
                if (lookup.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

}
