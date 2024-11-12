/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
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
public class Scan extends UnaryOperation {

    /**
     *
     * @param childOperation the child operation
     * @throws Exception
     */
    public Scan(Operation childOperation) throws Exception {
        super(childOperation);
    }

    @Override
    public String toString() {
        return "Scan";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a filter over the child operation
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new ScanIterator(processedTuples, withFilterDelegation);
    }

    /**
     * this class produces resulting tuples from a filter over the child
     * operation
     */
    public class ScanIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        public ScanIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, true);//pushes filter down to the child operation
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                if (lookup.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

}
