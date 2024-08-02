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
 * this filter finds tuples based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleColumnLookupFilter implements LookupFilter{

    /*
    * the column to be compared with
    */
    ColumnDescriptor column = null; 
    
    
    /*
    * the literal value to be compared with
    */
    Comparable value;
    
    /*
    * the comparison type
    */
    protected int comparisonType;
    
    
    
    /**
     *
     * @param col the name of the column to be placed at the left side of the comparison. The name can be prefixed by the table name (e.g. 'tab.col')
     * @param comparisonType the comparison type
     * @throws java.lang.Exception
     */
    public SingleColumnLookupFilter(String col, int comparisonType) throws Exception{
        this.comparisonType = comparisonType;
        column = new ColumnDescriptor(col);
    }
    
    /**
     *
     * @param table the name of the table to be placed at the left side of the comparison.
     * @param col the name of the column to be placed at the left side of the comparison. 
     * @param comparisonType the comparison type
     */
    public SingleColumnLookupFilter(String table, String col, int comparisonType){
        this.comparisonType = comparisonType;
        column = new ColumnDescriptor(table, col);
        
    }
    
    public SingleColumnLookupFilter(ColumnDescriptor column, int comparisonType){
        this.comparisonType = comparisonType;
        this.column = column;
        
    }
    
    public ColumnDescriptor getColumnDescriptor(){
        return column;
    }
            
    
    /**
     * @return the comparisonType
     */
    public int getComparisonType() {
        return comparisonType;
    }
    
    
    
    
    
    public void setValue(Comparable v){
        this.value = v;
    }
    
    /**
     *
     * @return the literal value placed at the right side of the comparison.
     */
    public Comparable getValue(){
        return value;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean match(Tuple tuple) {
        ibd.query.QueryStats.COMPARE_FILTER++;
        //compares the left side column against a right side value
        //return ComparisonTypes.match(tuple.rows[tupleIndex].getValue(column.getColumnName()), value, comparisonType);
        //System.out.println(tuple.rows[tupleIndex].getValue(colIndex));
        return ComparisonTypes.match(tuple.rows[column.getColumnLocation().rowIndex].getValue(column.getColumnLocation().colIndex), value, comparisonType);
    }
    
    @Override
    public String toString() {
        String col = column.getTableName()+"."+column.getColumnName();
        String compType = ComparisonTypes.getComparisonOperation(comparisonType);
        return col+compType+value;
    }
    
}
