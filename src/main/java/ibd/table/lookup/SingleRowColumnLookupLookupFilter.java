/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.lookup;

import ibd.query.lookup.SingleColumnLookupFilter;


/**
 * this filter finds rows based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleRowColumnLookupLookupFilter extends SingleRowColumnLookupFilter{

    /*
    * the filter that contains the filtered value
    */
    SingleColumnLookupFilter filter;

    public SingleRowColumnLookupLookupFilter(int colIndex, int comparisonType) throws Exception {
        super(colIndex, comparisonType);
    }
    
    
    public void setFilter(SingleColumnLookupFilter filter){
        this.filter = filter;
    }
    
    /**
     *
     * @return the filter than contains the value to be placed at the right side of the comparison.
     */
    public SingleColumnLookupFilter getFilter(){
        return filter;
    }

    @Override
    public Comparable getValue() {
        return filter.getValue();
    }
    
    
}
