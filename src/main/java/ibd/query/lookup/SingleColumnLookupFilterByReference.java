/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.ColumnLocation;
import ibd.table.ComparisonTypes;
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
public class SingleColumnLookupFilterByReference extends SingleColumnLookupFilter {

    //the descriptor of the column from where the reference value is taken
    ColumnDescriptor valueColumn = null;

    //the location of the processed tuple. It consider two factors:
    //  - the index of the processed tuple from the list of processed tuples
    //  - the index of the source tuple within the processed tuple
   // ColumnLocation tl;

    public SingleColumnLookupFilterByReference(String col, int comparisonType, String valueCol) throws Exception {
        super(col, comparisonType);
        valueColumn = new ColumnDescriptor(valueCol);
    }

    public SingleColumnLookupFilterByReference(String table, String col, int comparisonType, String valueCol) throws Exception {

        super(table, col, comparisonType);
        valueColumn = new ColumnDescriptor(valueCol);
    }

    /**
     * sets the location of the processed tuple from where the reference value
     * will be extracted
     *
     * @param op the operation is used in order to locate the propoer processed
     * tuple given its table alias (only the operation keeps track of the
     * mapping between tuples and tables).
     */
    public void setTupleLocation(Operation op) {
        try {
            op.setColumnLocationFromProcessedOperations(valueColumn);
        } catch (Exception ex) {
            Logger.getLogger(SingleColumnLookupFilterByReference.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        value = processedTuples.get(valueColumn.getColumnLocation().tupleIndex).rows[valueColumn.getColumnLocation().rowIndex].getValue(valueColumn.getColumnLocation().colIndex);

    }

    @Override
    public String toString() { 
        String col = column.getTableName() + "." + column.getColumnName();
        String compType = ComparisonTypes.getComparisonOperation(comparisonType);
        String col2 = valueColumn.getTableName() + "." + valueColumn.getColumnName();
        return col + compType + col2;
    }

}
