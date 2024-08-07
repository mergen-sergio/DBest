/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import ibd.query.binaryop.BinaryOperation;
import ibd.query.binaryop.join.Join;
import ibd.query.binaryop.join.MergeJoin;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class OperationUtils {

    public static List<Operation> findLeftSideCorrelations(Operation op){
        Operation op1 = op;
        List<Operation> operations = new ArrayList();
        while (op1!=null){
            if (op1 instanceof BinaryOperation){
                if (!(op1 instanceof Join)) return operations;
                if (op1 instanceof MergeJoin) return operations;
                operations.add(((BinaryOperation) op1).getLeftOperation());
            }
            op1 = op1.getParentOperation();
        }
        return operations;
    }
    
}
