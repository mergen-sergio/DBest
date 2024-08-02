/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This operation removes tuples whose value of an specified column is already
 * part of another accepted tuple.
 *
 * @author Sergio
 */
public class SourceRename extends UnaryOperation {

    String oldAlias;
    String newAlias;
    HashMap<String, String> old2NewPairs = new HashMap();

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @throws Exception
     */
    public SourceRename(Operation op, String oldAlias,String newAlias) throws Exception {
        super(op);
        this.oldAlias = oldAlias;
        this.newAlias = newAlias;
        old2NewPairs.put(oldAlias, newAlias);
    }
    
    
    
    @Override
    public void setDataSourcesInfo() throws Exception {
        childOperation.setDataSourcesInfo();
        ReferedDataSource s[] = childOperation.getDataSources();
        dataSources = new ReferedDataSource[s.length];
        
        int i = 0;
        for (ReferedDataSource referedDataSource : s) {
            ReferedDataSource newDataSource = new ReferedDataSource();
            newDataSource.prototype = referedDataSource.prototype;
            String newAlias_ = old2NewPairs.get(referedDataSource.alias);
            if (newAlias_==null)
                newAlias_ = referedDataSource.alias;
            
            newDataSource.alias = newAlias_;
            dataSources[i] = newDataSource;
            i++;
        }
        
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        Map<String,List<String>> newMap = new HashMap<String,List<String>>(); 
        Map<String,List<String>> oldMap = this.getChildOperation().getContentInfo();
        for (Map.Entry<String, List<String>> entry : oldMap.entrySet()) {
            String oldAlias_ = entry.getKey();
            String newAlias_ = old2NewPairs.get(oldAlias_);
            if (newAlias_==null)
                newAlias_ = oldAlias_;
            newMap.put(newAlias_, entry.getValue());
            
        }
        return newMap;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new SourceRenameIterator(processedTuples, withFilterDelegation);
    }

    

    @Override
    public String toString() {
        return "Source Rename:" + newAlias;
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class SourceRenameIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        
        public SourceRenameIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                
                
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (!lookup.match(tp)) {
                    continue;
                }
                return tp;

            }
            return null;
        }


    }
}
