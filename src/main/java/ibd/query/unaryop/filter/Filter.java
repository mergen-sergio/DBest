/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.filter;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilterByReference;
import ibd.query.lookup.TwoColumnsLookupFilter;
import ibd.query.unaryop.UnaryOperation;
import java.util.Iterator;
import java.util.List;

/**
 * A filter operation filters tuples that come from its child operation. This
 * operation defines the filter and asks for the child operation to resolve it.
 * The child operation apply the filter over all tuples that is generates. Is
 * the child operation is an index scan, and depending on the filters defined,
 * the search can be performed efficiently by index lookups.
 *
 * @author Sergio
 */
public class Filter extends UnaryOperation {

    LookupFilter filter = null;

    /**
     *
     * @param childOperation the child operation
     * @param filter the filter condition that needs to be satisfied
     * @throws Exception
     */
    public Filter(Operation childOperation, LookupFilter filter) throws Exception {
        super(childOperation);
        this.filter = filter;
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
        
        //sets the tuple index of the column based filters
        //setTupleIndex(filter);
        
    }
    
    @Override
    public LookupFilter getFilters(){
        return filter;
    };

    /*
    * Some filters refer to colum value comparisons. For those filters a tuple index is required to 
    * locate the tuple whose column will be compared.
    * The tuple index is set based on the table name information retrieved from the filter.
     */
    private void setTupleIndex(LookupFilter filter) {
        if (filter instanceof CompositeLookupFilter) {
            setTupleIndex((CompositeLookupFilter) filter);

        } else if (filter instanceof SingleColumnLookupFilter) {
            setTupleIndex((SingleColumnLookupFilter) filter);
        } else if (filter instanceof TwoColumnsLookupFilter) {
            setTupleIndex((TwoColumnsLookupFilter) filter);
        }
    }

    //sets the tuple indexes for all parts of this composite filter
    private void setTupleIndex(CompositeLookupFilter filter) {
        for (LookupFilter filter1 : filter.getFilters()) {
            setTupleIndex(filter1);
        }
    }

    //sets the tuple index for this single column filter
    private void setTupleIndex(SingleColumnLookupFilter filter) {
        try {

            //sets  the tuple index of the column to be filtered.
            childOperation.setColumnLocation(filter.getColumnDescriptor());

            //sets the tuple location of the column to be used as a reference value, if necessary
            if (filter instanceof SingleColumnLookupFilterByReference) {
                SingleColumnLookupFilterByReference f1 = (SingleColumnLookupFilterByReference) filter;
                f1.setTupleLocation(this);
            }

        } catch (Exception ex) {
        }
    }

    private void setTupleIndex(TwoColumnsLookupFilter filter) {
        try {

            //sets the tuple index of the left-side comparison column 
            childOperation.setColumnLocation(filter.getLeftColumn());
            //filter.setLeftTupleIndex(tupleIndex);

            //sets the tuple index of the right-side comparison column
            childOperation.setColumnLocation(filter.getRightColumn());
            //filter.setRightTupleIndex(tupleIndex);

        } catch (Exception ex) {
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a filter over the child operation
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new FilterOperationIterator(processedTuples,  withFilterDelegation);
    }

    /**
     * this class produces resulting tuples from a filter over the child
     * operation
     */
    public class FilterOperationIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        public FilterOperationIterator(List<Tuple> processedTuples,  boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());   

            if (filter instanceof SingleColumnLookupFilterByReference) {
                SingleColumnLookupFilterByReference f1 = (SingleColumnLookupFilterByReference) filter;
                f1.setValue(processedTuples);
            }

            tuples = childOperation.lookUp(processedTuples,  true);//pushes filter down to the child operation
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (lookup.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

    @Override
    public String toString() {
        return "Filter ("+filter+")";
    }
}
