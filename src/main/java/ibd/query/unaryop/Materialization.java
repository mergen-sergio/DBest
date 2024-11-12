/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This unary operation is materialized. It creates a hash containing all the
 * tuples that come from the child operation. Useful for lookups based on
 * equivalence conditions.
 *
 * @author Sergio
 */
public class Materialization extends UnaryOperation {

    /*
    materialized hash of tuples, using as key the columns that are part of conjunctive equality search conditions.
    It can answer lookups based on equivalence conditions efficiently.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    List<Tuple> tuples;

    
    
    boolean memoryUsedDefined = false;
    long memoryUsed = 0;

    /**
     *
     * @param childOperation the child operation
     * @throws Exception
     */
    public Materialization(Operation childOperation) throws Exception {
        super(childOperation);
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();

        //erases the previously built hash.
        //a new one is created when the first query is executed. 
        tuples = null;
        memoryUsedDefined = false;
    }

    

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new MaterializedIndexIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        return "Materialized";
    }

    /**
     * the class that materializes a collection of tuples that come from the
     * child operation using a hash. The query is answered using the hash.
     */
    public class MaterializedIndexIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> it = null;

        public MaterializedIndexIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {

            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            buildHash();
            it = tuples.iterator();
        }
        
        
        
        private void buildHash(){
        //build hash, if one does not exist yet
            if (tuples == null) {
                tuples = new ArrayList<>();
                memoryUsed = 0;
                int tupleSize = 0;
                try {
                    tupleSize = childOperation.getTupleSize();
                } catch (Exception ex) {
                }
                    //accesses and indexes all tuples that come from the child operation
                    it = childOperation.lookUp(processedTuples, false);
                    
                    while (it.hasNext()) {
                        Tuple tuple = (Tuple) it.next();
                        tuples.add(tuple);
                    }

                memoryUsed+=tuples.size()*tupleSize;
                QueryStats.MEMORY_USED += memoryUsed;
            }

            
        }

        @Override
        protected Tuple findNextTuple() {
            while (it.hasNext()) {
                Tuple tp = it.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                //only the unhashed filters need to be checked
                if (lookup.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

}
