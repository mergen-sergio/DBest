/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.ColumnDescriptor;
import ibd.query.Tuple;
import ibd.table.ComparisonTypes;


/**
 * this filter finds tuples based on a comparison between two columns. 
 * @author Sergio
 */
public class TwoColumnsLookupFilter implements LookupFilter{

    /*
    * the left column to be compared with
    */
    ColumnDescriptor leftColumn = null;
    
    /*
    * the right column to be compared with
    */
    ColumnDescriptor rightColumn = null;
    
    /*
    * the comparison type
    */
    protected int comparisonType;
    
    /*
    * the identification of the left source tuple from where the column is to be retrieved.
    */
    //protected int leftTupleIndex;
    
    /*
    * the identification of the right source tuple from where the column is to be retrieved.
    */
    //protected int rightTupleIndex;
    
    /**
     *
     * @param leftCol the left column to be compared with. It may contain the table as a prefix (e.g. 'tab.col')
     * @param rightCol the right column to be compared with. It may contain the table as a prefix (e.g. 'tab.col')
     * @param comparisonType the comparison type
     * @throws java.lang.Exception
     */
    public TwoColumnsLookupFilter(String leftCol, String rightCol, int comparisonType) throws Exception{
        this.comparisonType = comparisonType;
        leftColumn = new ColumnDescriptor(leftCol);
        rightColumn = new ColumnDescriptor(rightCol);
    }
    
    /**
     *
     * @param leftTable the left table whose column is to be compared
     * @param leftCol the left column to be compared with 
     * @param rightTable the right table whose column is to be compared
     * @param rightCol the right column to be compared with 
     * @param comparisonType the comparison type
     */
    public TwoColumnsLookupFilter(String leftTable, String leftCol, String rightTable, String rightCol, int comparisonType){
        this.comparisonType = comparisonType;
        leftColumn = new ColumnDescriptor(leftTable, leftCol);
        rightColumn = new ColumnDescriptor(rightTable, rightCol);
        
    }
    
    public TwoColumnsLookupFilter(ColumnDescriptor leftCol, ColumnDescriptor rightCol, int comparisonType){
        this.comparisonType = comparisonType;
        leftColumn = leftCol;
        rightColumn = rightCol;
        
    }
    
    
    /**
     * @return the comparisonType
     */
    public int getComparisonType() {
        return comparisonType;
    }
    
    /**
     * @return the tupleIndex
     */
//    public int getLeftTupleIndex() {
//        return leftTupleIndex;
//    }
    
    /**
     * Sets the left tuple index. 
     * This information needs to be set before the filter is performed by the match function
     * @param tupleIndex
     */
//    public void setLeftTupleIndex(int tupleIndex) {
//        this.leftTupleIndex = tupleIndex;
//    }
    
    /**
     * Sets the right tuple index. 
     * This information needs to be set before the filter is performed by the match function
     * @param tupleIndex
     */
//    public void setRightTupleIndex(int tupleIndex) {
//        this.rightTupleIndex = tupleIndex;
//    }
    
    /**
     *
     * @return the column to be compared with
     */
    public ColumnDescriptor getLeftColumn(){
        return leftColumn;
    }
    
    public ColumnDescriptor getRightColumn(){
        return rightColumn;
    }
    
    /**
     *
     * @return the table whose column is to be compared
     */
    public String getLeftTable(){
        return leftColumn.getTableName();
    }
    
    public String getRightTable(){
        return rightColumn.getTableName();
    }
    
    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean match(Tuple tuple) {
        ibd.query.QueryStats.COMPARE_FILTER++;
        //compares the column that comes from a source tuple against a value
        Comparable value1 = tuple.rows[leftColumn.getColumnLocation().rowIndex].getValue(leftColumn.getColumnName());
        Comparable value2 = tuple.rows[rightColumn.getColumnLocation().rowIndex].getValue(rightColumn.getColumnName());
        return ComparisonTypes.match(value1, value2, comparisonType);
    }
    
    @Override
    public String toString() {
        String col1 = leftColumn.getTableName()+"."+leftColumn.getColumnName();
        String compType = ComparisonTypes.getComparisonOperation(comparisonType);
        String col2 = rightColumn.getTableName()+"."+rightColumn.getColumnName();
        return col1+compType+col2;
    }
    
}
