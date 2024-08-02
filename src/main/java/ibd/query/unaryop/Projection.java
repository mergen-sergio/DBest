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
import ibd.query.unaryop.sort.Sort;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;
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
public class Projection extends UnaryOperation {

    boolean isOrdered;

    String alias;
    String[] projectionColumns_;
    List<ColumnDescriptor> projectionColumns;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @param columns the list of columns that will form the non-duplicated
     * tuples. The name can be prefixed by the table name (e.g. tab.col)
     * @param isOrdered indicates if the incoming tuples from the connected
     * operation are already ordered by the referenceColumn column
     * @throws Exception
     */
    public Projection(Operation op, String alias, String[] columns, boolean isOrdered) throws Exception {
        super(op);
        this.alias = alias;
        this.isOrdered = isOrdered;
        projectionColumns_ = columns;
        projectionColumns = new ArrayList();
        for (String col : columns) {
            ColumnDescriptor sortColumn = new ColumnDescriptor(col);
            projectionColumns.add(sortColumn);
        }

        //If the incoming tupled are not already sorted, an intermediary sort operation is added
//        if (!isOrdered) {
//            Sort cs = new Sort(childOperation, projectionColumns_, true);
//            childOperation = cs;
//        }

    }

    @Override
    public void prepare() throws Exception {
        
        super.prepare();
        
        setColumnsIndexes();
    }

    private void setColumnsIndexes() throws Exception {
        for (ColumnDescriptor col : projectionColumns) {
            //int index = childOperation.getRowIndex(col.getTableName());
            //col.setTupleIndex(index);
            childOperation.setColumnLocation(col);
        }
    }

    protected void setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        for (ColumnDescriptor col : projectionColumns) {
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
        List list = new ArrayList<String>();
        for (ColumnDescriptor colDescriptor : projectionColumns) {
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
        
        setPrototype();
    }

    @Override
    public String toString() {
        return "projection:" + projectionColumns;
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
                for (int i = 0; i < projectionColumns.size(); i++) {
                    ColumnDescriptor col = projectionColumns.get(i);
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

            }
            return null;
        }


    }
}
