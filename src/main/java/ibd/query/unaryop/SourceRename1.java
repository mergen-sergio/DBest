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
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This operation removes tuples whose value of an specified column is already
 * part of another accepted tuple.
 *
 * @author Sergio
 */
public class SourceRename1 extends UnaryOperation {

    String alias;
    List<ColumnDescriptor> columns;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @throws Exception
     */
    public SourceRename1(Operation op, String alias) throws Exception {
        super(op);
        this.alias = alias;

    }
    
    private void setColumns() throws Exception{
        ReferedDataSource childSources[] = getChildOperation().getDataSources();
        columns = new ArrayList();
        for (ReferedDataSource dataSource : childSources) {
            for (Column col : dataSource.prototype.getColumns()) {
                ColumnDescriptor colDesc = new ColumnDescriptor(dataSource.alias+"."+col.getName());
                columns.add(colDesc);
            }
            
        }
        
    }

    @Override
    public void prepare() throws Exception {
        
        super.prepare();
        
        setColumnsIndexes();
    }

    private void setColumnsIndexes() throws Exception {
        for (ColumnDescriptor col : columns) {
            //int index = childOperation.getRowIndex(col.getTableName());
            //col.setTupleIndex(index);
            childOperation.setColumnLocation(col);
        }
    }

    protected void setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        for (ColumnDescriptor col : columns) {
            childOperation.setColumnLocation(col);
            Column originalCol = childOperation.getDataSources()[col.getColumnLocation().rowIndex].prototype.getColumn(col.getColumnName());
            Column newCol = Prototype.cloneColumn(originalCol);
            prototype.addColumn(newCol);
        }

        dataSources[0].prototype = prototype;
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String,List<String>> map = new LinkedHashMap<>();
        
        try {
            if (columns==null || columns.size()==0){
                setDataSourcesInfo();
                setColumns();
            }
        } catch (Exception ex) {
        }
        List list = new ArrayList<String>();
        for (ColumnDescriptor colDescriptor : columns) {
            list.add(colDescriptor.getColumnName());
        }
        map.put(alias,list);
        return map;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new DuplicateRemovalIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void setDataSourcesInfo() throws Exception {
        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;

        childOperation.setDataSourcesInfo();
        
        setColumns();
        setPrototype();
    }

    @Override
    public String toString() {
        return "projection:" + columns;
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class DuplicateRemovalIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        
        public DuplicateRemovalIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                
                LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
                for (int i = 0; i < columns.size(); i++) {
                    ColumnDescriptor col = columns.get(i);
                    Comparable value = tp.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex);
                    row.setValue(i, value);
                }

                Tuple returnTp = new Tuple();
                returnTp.rows = new LinkedDataRow[1];
                returnTp.rows[0] = new LinkedDataRow();
                returnTp.rows[0] = row;
                
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (!lookup.match(returnTp)) {
                    continue;
                }
                return returnTp;

                //NOT WORKING: needs to adjust by comparing only the value of the reference column
//                if (prevTuple == null || (!equals(prevTuple, tp))) {
//                    prevTuple = tp;
//                    return tp;
//                }
            }
            return null;
        }

        private boolean equals(Tuple tt1, Tuple tt2) {
            for (ColumnDescriptor projectionColumn : columns) {
                Comparable value1 = tt1.rows[projectionColumn.getColumnLocation().rowIndex].getValue(projectionColumn.getColumnLocation().colIndex);
                Comparable value2 = tt2.rows[projectionColumn.getColumnLocation().rowIndex].getValue(projectionColumn.getColumnLocation().colIndex);
                int comp = value1.compareTo(value2);
                if (comp != 0) {
                    return false;
                }
            }

            return true;
        }

    }
}
