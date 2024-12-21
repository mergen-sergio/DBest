/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.Operation;
import ibd.query.Tuple;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this filter finds tuples based on a single column comparison against a
 * reference value. A reference value is a value of a column that belongs to a
 * tuple that was already processed
 *
 * @author Sergio
 */
public class SingleColumnLookupFilterByReferenceX extends SingleColumnLookupFilter {


    public SingleColumnLookupFilterByReferenceX(String col, int comparisonType, String valueCol) throws Exception {
        super(col, comparisonType);
        elem = new ReferencedElement(valueCol);
    }

    public SingleColumnLookupFilterByReferenceX(String table, String col, int comparisonType, String valueCol) throws Exception {

        super(table, col, comparisonType);
       elem = new ReferencedElement(valueCol);
    }

    

    /**
     * Sets the reference value. The value comes from a column of one of the
     * already processed tuples
     *
     * @param processedTuples the processed tuples from where the reference
     * column will be extracted
     */
    public void setValue(List<Tuple> processedTuples) {
        //value = processedTuples.get(tl.tupleIndex).rows[tl.rowIndex].getValue(valueColumn.getColumnName());
        //value = processedTuples.get(tl.tupleIndex).rows[tl.rowIndex].getValue(tl.colIndex);
        ((ReferencedElement)elem).setValue(processedTuples);

    }
    
    

}
