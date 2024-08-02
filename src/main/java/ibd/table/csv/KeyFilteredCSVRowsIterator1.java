/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.csv;

import ibd.table.lookup.RowLookupFilter;
import ibd.table.prototype.LinkedDataRow;
import sources.csv.InvalidCsvException;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class KeyFilteredCSVRowsIterator1 extends CSVRowsIterator {

    RowLookupFilter filter;
    LinkedDataRow pkRow;

    public KeyFilteredCSVRowsIterator1(CSVTable csvTable, LinkedDataRow pkRow, RowLookupFilter filter) throws InvalidCsvException {
        super(csvTable);
        this.filter = filter;
        this.pkRow = pkRow;
    }

    
    @Override
    protected boolean match(LinkedDataRow dataRow){
        return (filter.match(dataRow) && dataRow.compareTo(pkRow)==0);
    }
}
