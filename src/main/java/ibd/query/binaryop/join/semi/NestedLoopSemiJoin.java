/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.semi;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a semi nested loop join between the left and the right operations
 * using the terms provided by the join predicate.
 *
 * @author Sergio
 */
public class NestedLoopSemiJoin extends Join {

    //the filter that needs to be performed over the right side operation.
    CompositeLookupFilter joinFilter;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public NestedLoopSemiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public boolean useLeftSideLookups() {
        return true;
    }

    /**
     * {@inheritDoc }
     * the data sources array is a copy of the data sources that come from the
     * left subtree
     *
     * @throws Exception
     */
    @Override
    public void setDataSourcesInfo() throws Exception {

        getLeftOperation().setDataSourcesInfo();
        getRightOperation().setDataSourcesInfo();

        ReferedDataSource left[] = getLeftOperation().getDataSources();
        dataSources = new ReferedDataSource[left.length];
        System.arraycopy(left, 0, dataSources, 0, left.length);

    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        return getLeftOperation().getContentInfo();
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

    ;


    /**
     *
     * @return the name of the operation
     */
    @Override
    public String getJoinAlgorithm() {
        return "Nested Loop Semi Join";
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
     * the class that produces resulting tuples from the join between the two
     * underlying operations.
     */
    private class NestedLoopJoinIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;

        /**
         * @param processedTuples the tuples that come from operations already
         * processed. The columns from rows that are part of these tuples can be
         * used by the unprocessed operations, like for filtering.
         * @param lookup the condition from the parent operation that needs to
         * be satisfied
         */
        public NestedLoopJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
        }

        //fills the join filters with the left-side values that are necessary to perform the lookup.
        private void fillFilter(Tuple currentLeftTuple) {
            List<JoinTerm> joinTerms = joinPredicate.getTerms();
            int x = 0;
            for (LookupFilter filter : joinFilter.getFilters()) {
                JoinTerm joinTerm = joinTerms.get(x);
                SingleColumnLookupFilter f = (SingleColumnLookupFilter) filter;
                //Comparable value = currentLeftTuple.rows[joinTerm.getLeftTupleRowIndex()].getValue(f.getColumn());
                Comparable value = currentLeftTuple.rows[joinTerm.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(joinTerm.getLeftColumnDescriptor().getColumnLocation().colIndex);
                f.setValue(value);
                x++;

            }
        }

        /**
         *
         * @return the next satisfying tuple, if any
         */
        @Override
        protected Tuple findNextTuple() {

            while (leftTuples.hasNext()) {
                Tuple currentLeftTuple = leftTuples.next();

                //the computed rows from the current left side tuple can be used by the right part of the join
                processedTuples.add(currentLeftTuple);

                //the lookup conditions are filled with values taken from the computed rows from the current left side
                fillFilter(currentLeftTuple);

                //check if there exists a correpondendence on the right side
                boolean exists = rightOperation.exists(processedTuples, true);

                //the computed tuple from the left is removed from the processed list, since the right side of the join already finished its processing
                processedTuples.remove(processedTuples.size() - 1);

                if (exists) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple);
                    //a tuple must satisfy the lookup filter that comes from the parent operation
                    if (lookup.match(tuple)) {
                        return tuple;
                    }

                }

            }

            //no more left tuples to be joined
            return null;
        }
    }

}
