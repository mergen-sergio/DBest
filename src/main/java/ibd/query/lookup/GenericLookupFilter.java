/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.ComparisonTypes;


/**
 * this filter finds tuples based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class GenericLookupFilter implements LookupFilter{

    /*
    * the column to be compared with
    */
    Element element1 = null; 
    
    /*
    * the comparison type
    */
    protected int comparisonType;
    
    Element element2 = null; 
    
    
    public GenericLookupFilter(Element element1, int comparisonType, Element element2) throws Exception{
        this.comparisonType = comparisonType;
        this.element1 = element1;
        this.element2 = element2;
    }
    
    
    /**
     * @return the comparisonType
     */
    public int getComparisonType() {
        return comparisonType;
    }
    
    
    @Override
    public boolean match(Tuple tuple) {
        ibd.query.QueryStats.COMPARE_FILTER++;
        return ComparisonTypes.match(element1.getValue(tuple), element2.getValue(tuple), comparisonType);
    }
    
}
