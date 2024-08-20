/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This operation removes tuples whose value of an specified column is already
 * part of another accepted tuple.
 *
 * @author Sergio
 */
public class HashDuplicateRemoval extends UnaryOperation {


    HashMap<String, Tuple> allTuples;
    int tupleSize = 0;
    
    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param referenceColumn the name of the column to be used to remove
     * duplicates. The name can be prefixed by the table name (e.g. tab.col)
     * @param isOrdered indicates if the incoming tuples from the connected
     * operation are already ordered by the referenceColumn column
     * @throws Exception
     */
    public HashDuplicateRemoval(Operation op) throws Exception {
        super(op);
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
        tupleSize = childOperation.getTupleSize();
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashDuplicateRemovalIterator(processedTuples, withFilterDelegation);
    }
    
    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Hash Duplicate Removal";
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class HashDuplicateRemovalIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        
        //the iterator over the child operation
        Iterator<Tuple> allTuplesIterator;
        
        public HashDuplicateRemovalIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
            allTuples = new HashMap<>();
        }
        
        private String getKey(Tuple tuple){
            String key = "";
            for (LinkedDataRow row : tuple.rows) {
                for (int i = 0; i < row.getFieldsSize(); i++) {
                    Comparable value = row.getValue(i);
                    key += value;
                }
                
            }
            return key;
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (!lookup.match(tp)) {
                    continue;
                }
                String key = getKey(tp);
                allTuples.put(key, tp);
            }
            
            if (allTuplesIterator==null){
                allTuplesIterator = allTuples.values().iterator();
                QueryStats.MEMORY_USED += allTuples.size()*tupleSize;
            }
            
            while(allTuplesIterator.hasNext()){
                Tuple tp = allTuplesIterator.next();
                return tp;
            }
            
            
            return null;
        }

    }
}
