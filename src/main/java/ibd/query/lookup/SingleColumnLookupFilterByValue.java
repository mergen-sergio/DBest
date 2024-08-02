/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.ColumnDescriptor;
import ibd.table.ComparisonTypes;


/**
 * this filter finds tuples based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleColumnLookupFilterByValue extends SingleColumnLookupFilter{

    
    /**
     *
     * @param col the name of the column to be placed at the left side of the comparison. The name can be prefixed by the table name (e.g. 'tab.col')
     * @param value the literal value to be placed at the right side of the comparison
     * @param comparisonType the comparison type
     * @throws java.lang.Exception
     */
    public SingleColumnLookupFilterByValue(String col, int comparisonType, Comparable value) throws Exception{
        super(col, comparisonType);
        this.value = value; 
    }
    
    /**
     *
     * @param table the name of the table to be placed at the left side of the comparison.
     * @param col the name of the column to be placed at the left side of the comparison. 
     * @param value the literal value to be placed at the right side of the comparison.
     * @param comparisonType the comparison type
     */
    public SingleColumnLookupFilterByValue(String table, String col, int comparisonType, Comparable value){
        super(table, col, comparisonType);
        this.value = value;
    }
    
    public SingleColumnLookupFilterByValue(ColumnDescriptor col, int comparisonType, Comparable value){
        super(col, comparisonType);
        this.value = value;
    }
    
    
    
    
    @Override
    public String toString() {
        String col = column.getTableName()+"."+column.getColumnName();
        String compType = ComparisonTypes.getComparisonOperation(comparisonType);
        return col+compType+value;
    }
    
}
