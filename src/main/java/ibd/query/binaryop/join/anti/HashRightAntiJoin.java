/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.anti;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.binaryop.join.Join;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.JoinTerm;
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
public class HashRightAntiJoin extends Join {


    /*
    materialized hash of tuples, using as key the columns from the join predicate.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    HashMap<String, List<Tuple>> tuples;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public HashRightAntiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        //sets the column indexes for the terms of the join predicate
        setJoinTermsIndexes();

        //erases the previously built hash.
        //a new one is created when the first query is executed. 
        tuples = null;

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

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String getJoinAlgorithm() {
        return "Hash Right Anti Join";
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

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;

        TupleTraverser tupleTraverser = null;

        public HashJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);

            buildHash();

        }

        private void buildHash() {
            //build hash, if one does not exist yet
            if (tuples == null) {
                tuples = new HashMap();
                long memoryUsed = 0;
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
                QueryStats.MEMORY_USED += memoryUsed;
            }
        }

        //set the key with the left-side values that are necessary to perform the lookup.
        private String fillKey(Tuple currentLeftTuple) {
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

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (leftTuples.hasNext()) {
                Tuple currentLeftTuple = leftTuples.next();

                //the lookup conditions are filled with values taken from the computed rows from the current left side
                String key = fillKey(currentLeftTuple);
                tuples.remove(key);
            }

            if (tupleTraverser == null) {
                tupleTraverser = new TupleTraverser(tuples);
            }
            //iterate through the right side tuples that have no matches
            tupleTraverser = new TupleTraverser(tuples);
            while (tupleTraverser.hasNext()) {
                //sgbd.info.Query.COMPARE_JOIN++;
                Tuple curTuple2 = (Tuple) tupleTraverser.nextTuple();
                //create a returning tuple and add the joined tuples
                Tuple tuple = new Tuple();
                tuple.setSourceRows(curTuple2);
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (lookup.match(tuple)) {
                    return tuple;
                }

            }

            //no more tuples to be joined
            return null;
        }
    }

    class TupleTraverser {

        private final HashMap<String, List<Tuple>> tuples;
        private Iterator<Map.Entry<String, List<Tuple>>> mapIterator;
        private List<Tuple> currentList;
        private int currentIndex;

        public TupleTraverser(HashMap<String, List<Tuple>> tuples) {
            this.tuples = tuples;
            this.mapIterator = tuples.entrySet().iterator();
            this.currentList = null;
            this.currentIndex = 0;
        }

        public Tuple nextTuple() {
            // Check if the current list is exhausted or not initialized
            if (currentList == null || currentIndex >= currentList.size()) {
                // Move to the next entry in the map
                if (mapIterator.hasNext()) {
                    Map.Entry<String, List<Tuple>> entry = mapIterator.next();
                    currentList = entry.getValue();
                    currentIndex = 0;
                } else {
                    // No more tuples left
                    return null;
                }
            }

            // Get the next tuple from the current list
            Tuple nextTuple = currentList.get(currentIndex);
            currentIndex++;
            return nextTuple;
        }

        public boolean hasNext() {
            // Check if there are more tuples in the current list or in the map
            return (currentList != null && currentIndex < currentList.size()) || mapIterator.hasNext();
        }
    }

}
