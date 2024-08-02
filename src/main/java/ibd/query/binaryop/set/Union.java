/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.set;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.query.fields.Field;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class Union extends Set {

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @throws Exception
     */
    public Union(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Union";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new UnionIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the union between the two
     * underlying operations.
     */
    private class UnionIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current tuple read
        Tuple leftTuple;

        Tuple rightTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;

        /**
         * @param processedTuples the tuples that come from operations already
         * processed. The columns from these tuples can be used by the
         * unprocessed operations, like for filtering.
         * @param lookup the condition from the parent operation that needs to
         * be satisfied
         */
        public UnionIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            leftTuple = null;
            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
        }

        /**
         *
         * @return the next satisfying tuple, if any
         */
        @Override
        protected Tuple findNextTuple() {

            if (leftTuple == null) {
                if (leftTuples.hasNext()) {
                    while (leftTuples.hasNext()) {
                        leftTuple = leftTuples.next();
                        if (lookup.match(leftTuple)) {
                            break;
                        }
                    }
                }
            }

            if (rightTuple == null) {
                while (rightTuples.hasNext()) {
                    rightTuple = rightTuples.next();
                    if (lookup.match(rightTuple)) {
                        break;
                    }
                }
            }

            if (leftTuple == null && rightTuple == null) {
                return null;
            }

            if (leftTuple != null && rightTuple != null
                    && (leftTuple.compareTo(rightTuple) == 0)) {
                Tuple tuple = new Tuple();
                tuple.setSourceRows(leftTuple);
                leftTuple = null;
                rightTuple = null;
                return tuple;
            } else if (leftTuple != null && (rightTuple == null
                    || (leftTuple.compareTo(rightTuple) < 0))) {
                Tuple tuple = new Tuple();
                tuple.setSourceRows(leftTuple);
                leftTuple = null;
                return tuple;
            } else {
                Tuple tuple = new Tuple();
                tuple.setSourceRows(rightTuple);
                rightTuple = null;
                return tuple;
            }
        }

//        private int compare(Tuple t1, Tuple t2) {
//            for (int i = 0; i < t1.rows.length; i++) {
//                LinkedDataRow st1 = t1.rows[i];
//                LinkedDataRow st2 = t2.rows[i];
//                for (int j = 0; i < st1.getFieldsSize(); i++) {
//                    Field f1 = st1.getField(j);
//                    Field f2 = st2.getField(j);
//                    int comp = f1.compareTo(f2);
//                    if (comp != 0) {
//                        return comp;
//                    }
//                }
//            }
//            return 0;
//        }

    }

}
