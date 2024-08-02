/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.unaryop.sort.Sort;
import java.util.Iterator;
import java.util.List;

/**
 * This operation removes tuples whose value of an specified column is already
 * part of another accepted tuple.
 *
 * @author Sergio
 */
public class DuplicateRemoval extends UnaryOperation {


    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param referenceColumn the name of the column to be used to remove
     * duplicates. The name can be prefixed by the table name (e.g. tab.col)
     * @param isOrdered indicates if the incoming tuples from the connected
     * operation are already ordered by the referenceColumn column
     * @throws Exception
     */
    public DuplicateRemoval(Operation op) throws Exception {
        super(op);
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new DuplicateRemovalIterator(processedTuples, withFilterDelegation);
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class DuplicateRemovalIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        Tuple prevTuple = null;

        public DuplicateRemovalIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (!lookup.match(tp)) {
                    continue;
                }
                if (prevTuple == null || (!tp.equals(prevTuple))) {
                    prevTuple = tp;
                    return tp;
                }

            }
            return null;
        }

    }
}
