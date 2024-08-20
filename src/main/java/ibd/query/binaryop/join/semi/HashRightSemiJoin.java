/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.semi;

import ibd.query.binaryop.join.outer.*;
import ibd.query.binaryop.join.*;
import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a nested loop join between the left and the right operations using
 * the terms provided by the join predicate.
 *
 * @author Sergio
 */
public class HashRightSemiJoin extends Join {


    /*
    materialized hash of tuples, using as key the columns from the join predicate.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    HashMap<String, List<Tuple>> tuples;


    //a null tuple that is shared with all left side tuples that fail to join with right side tuples.
    Tuple nullLeftTuple;

    boolean memoryUsedDefined = false; 
    long memoryUsed = 0;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public HashRightSemiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        //sets the column indexes for the terms of the join predicate
        setJoinTermsIndexes();

        setNullLeftTuple();

        //erases the previously built hash.
        //a new one is created when the first query is executed. 
        tuples = null;

        memoryUsedDefined = false;
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

        ReferedDataSource right[] = getRightOperation().getDataSources();
        dataSources = new ReferedDataSource[right.length];
        System.arraycopy(right, 0, dataSources, 0, right.length);

    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        return getRightOperation().getContentInfo();
    }

    //sets the column indexes for the terms of the join predicate
    private void setJoinTermsIndexes() throws Exception {
        for (JoinTerm term : joinPredicate.getTerms()) {
            leftOperation.setColumnLocation(term.getLeftColumnDescriptor());
            rightOperation.setColumnLocation(term.getRightColumnDescriptor());
        }
    }

    protected void setNullLeftTuple() throws Exception {
        ReferedDataSource left[] = getLeftOperation().getDataSources();
        nullLeftTuple = new Tuple();
        nullLeftTuple.rows = new LinkedDataRow[left.length];
        for (int i = 0; i < left.length; i++) {
            LinkedDataRow row = new LinkedDataRow(left[i].prototype, false);
            nullLeftTuple.rows[i] = new LinkedDataRow();
            nullLeftTuple.rows[i] = row;
        }

    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Hash Right Join";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the nested loop join
     * between the two underlying operations.
     */
    private class HashJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current left tuple read
        Tuple currentLeftTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;


        public HashJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            currentLeftTuple = null;
            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);

            buildHash();

        }

        private void buildHash() {
            //build hash, if one does not exist yet
            if (tuples == null) {
                tuples = new HashMap();
                memoryUsed = 0;
                try {
                    //accesses and indexes all tuples that come from the child operation
                    Iterator<Tuple> it = rightOperation.lookUp(processedTuples, false);
                    int tupleSize = rightOperation.getTupleSize();
                    while (it.hasNext()) {
                        Tuple tuple = (Tuple) it.next();
                        String key = "";
                        for (JoinTerm term : joinPredicate.getTerms()) //for (SingleColumnLookupFilter lookupFilter : hashedFilters) 
                        {
                            //ColumnDescriptor col = lookupFilter.getColumnDescriptor();
                            ColumnDescriptor col = term.getRightColumnDescriptor();
                            key += tuple.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex).toString();
                        }

                        //String key = tuple.rows[hashColumn.getColumnLocation().rowIndex].getValue(hashColumn.getColumnName()).toString();
                        List tupleList = tuples.get(key);
                        if (tupleList == null) {
                            tupleList = new ArrayList();
                            tuples.put(key, tupleList);
                        }
                        tupleList.add(tuple);
                        memoryUsed += tupleSize;
                    }

                } catch (Exception ex) {
                }

            }
        }

        //set the key with the left-side values that are necessary to perform the lookup.
        private String fillKey() {
            String key = "";
            List<JoinTerm> joinTerms = joinPredicate.getTerms();
            //List<LookupFilter> filters = joinFilter.getFilters();
            for (int i = 0; i < joinTerms.size(); i++) {
                JoinTerm joinTerm = joinTerms.get(i);
                Comparable value = currentLeftTuple.rows[joinTerm.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(joinTerm.getLeftColumnDescriptor().getColumnLocation().colIndex);
                key += value;

            }
            return key;
        }

        @Override
        protected Tuple findNextTuple() {

            if (!memoryUsedDefined) {
                memoryUsedDefined = true;
                QueryStats.MEMORY_USED += memoryUsed;
            }

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (currentLeftTuple != null || leftTuples.hasNext()) {
                if (currentLeftTuple == null) {
                    currentLeftTuple = leftTuples.next();

                    //the lookup conditions are filled with values taken from the computed rows from the current left side
                    String key = fillKey();
                    List<Tuple> result = tuples.get(key);
                        if (result != null) {
                            tuples.remove(key);
                        }
                    if (result != null) {
                        rightTuples = result.iterator();
                    } else {
                        rightTuples = new ArrayList<Tuple>().iterator();
                    }

                }

                //iterate through the right side tuples that satisfy the lookup
                while (rightTuples.hasNext()) {
                    //sgbd.info.Query.COMPARE_JOIN++;
                    Tuple curTuple2 = (Tuple) rightTuples.next();
                    //create a returning tuple and add the joined tuples
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows( curTuple2);
                    //a tuple must satisfy the lookup filter that comes from the parent operation
                    if (lookup.match(tuple)) {
                        return tuple;
                    }

                }
                //All corresponding tuples from the right side processed. 
                //set null to allow left side cursor to advance
                currentLeftTuple = null;

            }

            //no more tuples to be joined
            return null;
        }
    }

}
