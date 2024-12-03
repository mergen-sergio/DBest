/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.outer;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.query.binaryop.join.Join;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.JoinTerm;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilterByValue;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.LinkedDataRow;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a nested loop join between the left and the right operations using
 * the join conditions provided by the join terms.
 *
 * @author Sergio
 */
public class NestedLoopLeftJoin extends Join {

    //the filter that needs to be performed over the right side operation.
    CompositeLookupFilter joinFilter;

    //a null tuple that is shared with all left side tuples that fail to join with right side tuples
    Tuple nullRightTuple;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param terms the the terms of the join predicate
     * @throws Exception
     */
    public NestedLoopLeftJoin(Operation leftOperation, Operation rightOperation, JoinPredicate terms) throws Exception {
        super(leftOperation, rightOperation, terms);
    }

    @Override
    public boolean useLeftSideLookups() {
        return true;
    }

    /**
     *
     * @return the join filters
     */
    @Override
    public LookupFilter getFilters() {
        return joinFilter;
    }

    @Override
    public void prepare() throws Exception {
        
        createJoinFilter();
        
        super.prepare();
        
        //sets the row indexes for the terms of the join predicate
        setJoinTermsIndexes();
        setNullRightTuple();
    }

    //sets the row indexes for the terms of the join predicate
    private void setJoinTermsIndexes() throws Exception {
        for (JoinTerm term : joinPredicate.getTerms()) {
            leftOperation.setColumnLocation(term.getLeftColumnDescriptor());
            rightOperation.setColumnLocation(term.getRightColumnDescriptor());
        }
    }

    //creates the single filter condition that will be pushed down to the right side operation. 
    //each time a left-sude tuple performs a lookup on the right side, ths filter is reused. 
    //Only the look-up value is replaced.
    private void createJoinFilter() {
        joinFilter = new CompositeLookupFilter(CompositeLookupFilter.AND);
        for (JoinTerm term : joinPredicate.getTerms()) {
            //Comparable leftValue = currentLeftTuple.sourceTuples[term.getLeftTupleIndex()].getValue(term.getLeftColumn());
            SingleColumnLookupFilterByValue f = new SingleColumnLookupFilterByValue(term.getRightColumnDescriptor(), ComparisonTypes.EQUAL, 0);
            //f.setTupleIndex(term.getRightTupleRowIndex());
            joinFilter.addFilter(f);
        }
    }

    protected void setNullRightTuple() throws Exception {
        ReferedDataSource right[] = getRightOperation().getDataSources();
        nullRightTuple = new Tuple();
        nullRightTuple.rows = new LinkedDataRow[right.length];
        for (int i = 0; i < right.length; i++) {
            LinkedDataRow row = new LinkedDataRow(right[i].prototype, false);
            nullRightTuple.rows[i] = new LinkedDataRow();
            nullRightTuple.rows[i] = row;
        }

    }

    @Override
    public String getJoinAlgorithm() {
        return "Nested Loop Left Join";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a left nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new LeftNestedLoopJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the left join between the two
     * underlying operations.
     */
    private class LeftNestedLoopJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current tuple read
        Tuple currentLeftTuple;

        boolean foundJoin = false;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;

        public LeftNestedLoopJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            currentLeftTuple = null;

            //leftTuples = leftOperation.run(); //iterate over all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples that comes from the left side
        }

        //fills the join filters with the values that are necessary to perform the lookup.
        private boolean fillFilter() {
            List<JoinTerm> joinTerms = joinPredicate.getTerms();
            int x = 0;
            for (LookupFilter filter : joinFilter.getFilters()) {
                JoinTerm joinTerm = joinTerms.get(x);
                SingleColumnLookupFilter f = (SingleColumnLookupFilter) filter;
                Comparable value = currentLeftTuple.rows[joinTerm.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(joinTerm.getLeftColumnDescriptor().getColumnLocation().colIndex);
                if (value==null) 
                    return false;
                f.setValue(value);
                x++;

            }
            return true;
        }

        /**
         *
         * @return the next satisfying tuple, if any
         */
        @Override
        protected Tuple findNextTuple() {

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (currentLeftTuple != null || leftTuples.hasNext()) {
                if (currentLeftTuple == null) {
                    currentLeftTuple = leftTuples.next();
                    foundJoin = false;

                    //the computed rows from the current left side tuple can be used by the right part of the join
                    processedTuples.add(currentLeftTuple);

                    //the lookup conditions are filled with values taken from the computed rows from the current left side
                    boolean hasAllJoinTerms = fillFilter();
                    
                    if (hasAllJoinTerms){
                        //lookup the target tuples from the right side
                        rightTuples = rightOperation.lookUp(processedTuples, true);
                    }
                    else rightTuples = Collections.emptyIterator();
                }

                //iterate through the right side tuples that satisfy the lookup
                while (rightTuples.hasNext()) {
                    Tuple curTuple2 = (Tuple) rightTuples.next();
                    //create returning tuple and add the joined tuples
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple, curTuple2);

                    foundJoin = true;
                    //a tuple must satisfy the lookup filter that comes from the parent operation
                    if (lookup.match(tuple)) {
                        return tuple;
                    }

                }
                
                //the left side tuples with no matches are complemented with null values
                if (!foundJoin) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple, nullRightTuple);
                    if (lookup.match(tuple)) {
                        currentLeftTuple = null;
                        processedTuples.remove(processedTuples.size() - 1);
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