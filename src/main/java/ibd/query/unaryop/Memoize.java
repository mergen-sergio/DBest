/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilterByValue;
import ibd.table.ComparisonTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This unary operation is materialized. It creates a hash containing all the
 * tuples that come from the child operation. Useful for lookups based on
 * equivalence over a primary key, and the child operation is not a source
 * operation.
 *
 * @author Sergio
 */
public class Memoize extends UnaryOperation {

    /*
    materialized hash of tuples, using as key the columns that are part of conjunctive equality search conditions.
    It can answer lookups based on equivalence conditions efficiently.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    HashMap<Integer, List<Tuple>> tuples = new HashMap();

    int cont;

    //ColumnDescriptor hashColumn;
    //int hashTupleIndex = -1;
    /**
     * the list of filters conditions that enable efficient table lookups
     */
    List<SingleColumnLookupFilter> lookupFilters;

    /**
     *
     * @param childOperation the child operation
     * @param col the name of the column used to create the hash index
     * @throws Exception
     */
    public Memoize(Operation childOperation, String col) throws Exception {
        super(childOperation);
        //hashColumn = new ColumnDescriptor(col);
    }

    @Override
    public LookupFilter getFilters() {
        if (parentOperation != null) {
            return parentOperation.getFilters();
        }
        return new NoLookupFilter();
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();

        //fills the lookupFilters list and checks whether those filters can be efficiently processed by the table
        setLookUp();

        //erases the previosulsy built hash.
        //a new one is created when the first query is executed. 
        tuples = null;
        //uses the table name to set the index of the tuple that contains the hash column
        //childOperation.getColumnLocation(hashColumn);
    }

    //fills the lookupFilters list and checks whether those filters can be efficiently processed by the table
    private void setLookUp() {
        if (parentOperation == null) {
            return;
        }

        if (parentOperation instanceof BinaryOperation) {
            BinaryOperation bop = (BinaryOperation) parentOperation;
            if (bop.useLeftSideLookups()) {
                if (bop.getLeftOperation().equals(this)) {
                    return;
                }
            } else {
                return;
            }

        }

        LookupFilter joinFilter = parentOperation.getFilters();
        lookupFilters = extractEqualFilters(joinFilter);

    }

    //Extract the equality filter conditions from a filter
    private List<SingleColumnLookupFilter> extractEqualFilters(LookupFilter filter) {
        if (filter instanceof CompositeLookupFilter) {
            return extractEqualFilters_((CompositeLookupFilter) filter);
        } else if (filter instanceof SingleColumnLookupFilter) {
            return extractEqualFilters_((SingleColumnLookupFilter) filter);
        } else {
            return new ArrayList();
        }
    }

    //Extract the equality filter conditions from a composed filter
    private List<SingleColumnLookupFilter> extractEqualFilters_(CompositeLookupFilter filter) {
        List<SingleColumnLookupFilter> columns = new ArrayList();
        for (LookupFilter f : filter.getFilters()) {
            if (f instanceof SingleColumnLookupFilterByValue) {
                SingleColumnLookupFilterByValue f1 = (SingleColumnLookupFilterByValue) f;
                if (f1.getComparisonType() == ComparisonTypes.EQUAL) {
                    columns.add(f1);
                }
            }
        }

        return columns;
    }

    //Extract the equality filter conditions from a single column filter
    private List<SingleColumnLookupFilter> extractEqualFilters_(SingleColumnLookupFilter filter) {

        List<SingleColumnLookupFilter> columns = new ArrayList();
        if (filter.getComparisonType() == ComparisonTypes.EQUAL) {
            columns.add(filter);
        }

        return columns;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new MaterializedIndexIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        return "Memoize";
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

            //build hash, if one does not exist yet
            if (tuples == null) {
                tuples = new HashMap();
            }
            {
                try {
                    //accesses and indexes all tuples that come from the child operation
                    Integer key = 0;

                    for (SingleColumnLookupFilter filter : lookupFilters) {
                        //row.setValue(filter.getColumn(), (Integer) filter.getValue());
                        key += (Integer) filter.getValue();
                    }
                    List tupleList = tuples.get(key);
                    if (tupleList != null) {
                        //System.out.println("found key "+key);

                        it = tupleList.iterator();
                        return;
                    }
                    //System.out.println("not found key "+key);
                    //System.out.println("count "+cont++);
                    tupleList = new ArrayList();
                    tuples.put(key, tupleList);

                    it = childOperation.lookUp(processedTuples, true);
                    while (it.hasNext()) {
                        Tuple tuple = (Tuple) it.next();
                        tupleList.add(tuple);
                    }
                    it = tupleList.iterator();

                } catch (Exception ex) {
                }
            }
            /*
            //here is where we build an iterator to traverse the query results
            //if the query has a equivalence over the pk filter, the index can be used
            //if (lookup instanceof SingleColumnLookupFilterByValue) 
            if (lookupFilters.size() > 0) {
                Integer key = 0;
                for (SingleColumnLookupFilter lookupFilter : lookupFilters) {
                    key += (Integer) lookupFilter.getValue();
                }

                //SingleColumnLookupFilterByValue pklf = (SingleColumnLookupFilterByValue) lookup;
                //if (pklf.getComparisonType() == ComparisonTypes.EQUAL && pklf.getColumnDescriptor().getColumnName().equals(hashColumn.getColumnName())) {
                //use the hash to find results based on the lookup key
                //ArrayList<Tuple> result = new ArrayList<>();
                //Tuple t = tuples.get(pklf.getValue().toString());
                List<Tuple> result = tuples.get(key);

                //the iterator now accesses only the tuples that satisfy the pk filter
                if (result != null) {
                    it = result.iterator();
                } else {
                    it = new ArrayList<Tuple>().iterator();
                }
                return;
            }
             */
            //if the filter is not equivalence over the pk field, we need to access all tuples from the hash
            //it = tuples.values().iterator();

        }

        @Override
        protected Tuple findNextTuple() {
            while (it.hasNext()) {
                Tuple tp = it.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                //if the filter is equivalence over the pk, all found tuples will match
                //if (lookup.match(tp)) 
                {
                    return tp;
                }

            }
            return null;
        }

    }

}
