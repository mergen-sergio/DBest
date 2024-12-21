/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;


/**
 * An empty implementation of lookup filter used to indicate that no filter needs to be satisfied
 * @author Sergio
 */
public class NoLookupFilter implements LookupFilter{

    @Override
    public boolean match(Tuple tuple) {
        return true; 
    }
    
    @Override
    public boolean match(LinkedDataRow row) {
        return true; 
    }

    
}
