/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.csv;

import engine.info.Parameters;
import ibd.table.prototype.LinkedDataRow;
import java.util.Iterator;
import sources.csv.InvalidCsvException;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public abstract class CSVRowsIterator implements Iterator<LinkedDataRow> {

    LinkedDataRow nextValue = null;
    Iterator<String[]> iterator;
    CSVTable csvTable;
    long pk = 0L;

    public CSVRowsIterator(CSVTable csvTable) throws InvalidCsvException {
        this.csvTable = csvTable;
        iterator = csvTable.recognizer.iterator();
    }

    /**
     * Finds the next value. If the next value was already computed by
     * hasNext(), use it.
     *
     * @return
     */
    @Override
    public LinkedDataRow next() {

        if (nextValue != null) {
            LinkedDataRow next_ = nextValue;
            nextValue = null;
            return next_;
        }

        return findNextTuple();

    }

    /**
     * verifies if there is a next value. Is there is one, store it so the call
     * to next may access it.
     *
     * @return true if there is a next value
     */
    @Override
    public boolean hasNext() {

        if (nextValue != null) {
            return true;
        }
        nextValue = findNextTuple();

        return (nextValue != null);
    }
/**
     * This is the function that actually locates the next value.
     *
     * @return the next value, or null if there isnt any.
     */
    protected LinkedDataRow findNextTuple(){
    while (iterator.hasNext()) {
            // Iterate through the dictionary of each node
            String[] data = iterator.next();
            if (data == null) {
                return null;
            }
            pk++;

            String[] columns = csvTable.recognizer.getColumnNames();
            LinkedDataRow dataRow = csvTable.convertRow(columns, data, pk);
            Parameters.RECORDS_READ++;
           if (match(dataRow)) {
                nextValue = dataRow;
                return nextValue;
            } 

        }

        return null;
    
    
    }
    
    protected abstract boolean match(LinkedDataRow dataRow);
}
