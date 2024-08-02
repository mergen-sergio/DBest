/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.lookup;


/**
 * this filter finds rows based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleRowColumnValueLookupFilter extends SingleRowColumnLookupFilter{

    /*
    * the literal value to be compared with
    */
    Comparable value;

    public SingleRowColumnValueLookupFilter(int colIndex, int comparisonType) throws Exception {
        super(colIndex, comparisonType);
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
    
    
}
