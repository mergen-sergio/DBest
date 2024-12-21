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
public class SingleColumnLookupFilterByValueX extends SingleColumnLookupFilter{

    
    
    /**
     *
     * @param col the name of the column to be placed at the left side of the comparison. The name can be prefixed by the table name (e.g. 'tab.col')
     * @param value the literal value to be placed at the right side of the comparison
     * @param comparisonType the comparison type
     * @throws java.lang.Exception
     */
    public SingleColumnLookupFilterByValueX(String col, int comparisonType, Comparable value) throws Exception{
        super(col, comparisonType);
        this.elem = new LiteralElement(value); 
    }
    
    /**
     *
     * @param table the name of the table to be placed at the left side of the comparison.
     * @param col the name of the column to be placed at the left side of the comparison. 
     * @param value the literal value to be placed at the right side of the comparison.
     * @param comparisonType the comparison type
     */
    public SingleColumnLookupFilterByValueX(String table, String col, int comparisonType, Comparable value){
        super(table, col, comparisonType);
        this.elem = new LiteralElement(value); 
    }
    
    public SingleColumnLookupFilterByValueX(ColumnDescriptor col, int comparisonType, Comparable value){
        super(col, comparisonType);
        this.elem = new LiteralElement(value); 
    }
    
    public void setValue(Comparable v){
        this.elem = new LiteralElement(v); 
    }
    
    
    
    
    
}
