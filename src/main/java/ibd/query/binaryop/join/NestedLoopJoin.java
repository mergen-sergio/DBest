/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilterByValue;
import ibd.table.ComparisonTypes;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a nested loop join between the left and the right operations using
 * the terms provided by the join predicate.
 *
 * @author Sergio
 */
public class NestedLoopJoin extends Join {

    //the filter that needs to be performed over the right side operation.
    CompositeLookupFilter joinFilter;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public NestedLoopJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public boolean useLeftSideLookups() {
        return true;
    }

    @Override
    public void prepare() throws Exception {
        
        //creates the single filter condition that will be pushed down to the right side operation.
        createJoinFilter();
        
        super.prepare();
        
        //sets the column indexes for the terms of the join predicate
        setJoinTermsIndexes();
    }

    //sets the column indexes for the terms of the join predicate
    private void setJoinTermsIndexes() throws Exception {
        for (JoinTerm term : joinPredicate.getTerms()) {
            leftOperation.setColumnLocation(term.getLeftColumnDescriptor());
            rightOperation.setColumnLocation(term.getRightColumnDescriptor());
        }
    }

    //creates the single filter condition that will be pushed down to the right side operation. 
    //each time a left-side tuple performs a lookup on the right side, ths filter is reused. 
    //Only the look-up value coming from the left is replaced, in the moment of the lookup.
    private void createJoinFilter() {
        joinFilter = new CompositeLookupFilter(CompositeLookupFilter.AND);
        for (JoinTerm term : joinPredicate.getTerms()) {
            SingleColumnLookupFilterByValue f = new SingleColumnLookupFilterByValue(term.getRightColumnDescriptor(), ComparisonTypes.EQUAL, 0);
            joinFilter.addFilter(f);
        }
    }

    /**
     *
     * @return the join filters
     */
    @Override
    public LookupFilter getFilters() {
        return joinFilter;
    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String getJoinAlgorithm() {
        return "Nested Loop Join";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new NestedLoopJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the nested loop join between the two
     * underlying operations.
     */
    private class NestedLoopJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current left tuple read
        Tuple currentLeftTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;

        public NestedLoopJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            currentLeftTuple = null;
            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
        }

        //fills the join filters with the left-side values that are necessary to perform the lookup.
        private void fillFilter() {
            List<JoinTerm> joinTerms = joinPredicate.getTerms();
            List<LookupFilter> filters = joinFilter.getFilters();
            for (int i = 0; i < filters.size(); i++) {
                LookupFilter filter = filters.get(i);
                JoinTerm joinTerm = joinTerms.get(i);
                SingleColumnLookupFilter f = (SingleColumnLookupFilter) filter;
                //Comparable value = currentLeftTuple.rows[joinTerm.getLeftTupleRowIndex()].getValue(f.getColumn());
                Comparable value = currentLeftTuple.rows[joinTerm.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(joinTerm.getLeftColumnDescriptor().getColumnLocation().colIndex);
                f.setValue(value);

            }
        }

        @Override
        protected Tuple findNextTuple() {

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (currentLeftTuple != null || leftTuples.hasNext()) {
                if (currentLeftTuple == null) {
                    currentLeftTuple = leftTuples.next();

                    //the computed rows from the current left side tuple can be used by the right part of the join
                    processedTuples.add(currentLeftTuple);

                    //the lookup conditions are filled with values taken from the computed rows from the current left side
                    fillFilter();
                    //lookup the tuples from the right side
                    rightTuples = rightOperation.lookUp(processedTuples, true);

                    
                }

                //iterate through the right side tuples that satisfy the lookup
                while (rightTuples.hasNext()) {
                    //sgbd.info.Query.COMPARE_JOIN++;
                    Tuple curTuple2 = (Tuple) rightTuples.next();
                    //create a returning tuple and add the joined tuples
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple, curTuple2);
                    //a tuple must satisfy the lookup filter that comes from the parent operation
                    if (lookup.match(tuple)) {
                        return tuple;
                    }

                }
                //All corresponding tuples from the right side processed. 
                //set null to allow left side cursor to advance
                currentLeftTuple = null;
                
                //the computed tuple from the left is removed from the processed list, since the right side of the join already finished its processing
                processedTuples.remove(processedTuples.size() - 1);
            }

            //no more tuples to be joined
            return null;
        }
    }

}
