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
public class Limit extends UnaryOperation {

    //int tuplesToReadX = -1;
    //int startingTupleX = -1;
    
    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param referenceColumn the name of the column to be used to remove
     * duplicates. The name can be prefixed by the table name (e.g. tab.col)
     * @param isOrdered indicates if the incoming tuples from the connected
     * operation are already ordered by the referenceColumn column
     * @throws Exception
     */
    public Limit(Operation op, int tuplesToRead, int startingTuple) throws Exception {
        super(op);
        this.tuplesToRead = tuplesToRead;
        this.startingTuple = startingTuple;
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        //childOperation.setPageInfo(startingTupleX, tuplesToReadX);
        Iterator<Tuple> tuples =  childOperation.lookUp(processedTuples, withFilterDelegation);
        //childOperation.setPageInfo(-1, -1);
        return tuples;
    }

}
