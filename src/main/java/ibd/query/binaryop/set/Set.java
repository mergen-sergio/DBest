/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.set;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.binaryop.BinaryOperation;
import java.util.List;
import java.util.Map;

/**
 * Performs a join between the left and the right operations using
 * the join conditions provided by join terms.
 *
 * @author Sergio
 */
public abstract class Set extends BinaryOperation {

    public Set(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
    }

    
    /**
     * {@inheritDoc }
     * the data sources array  is a copy of the data sources
     * that come from the left subtree
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
        return leftOperation.getContentInfo();
    }

}
