/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.unaryop.UnaryOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sort operation sorts tuples that come from its child operation. Tuples are
 * sorted according to the value of a specified column. This operation is
 * materialized.
 *
 * @author Sergio
 */
public class Sort extends UnaryOperation {

    /*
    * a materialized shared collection of objects.
    * all queries over this operation share the same collection.
     */
    ArrayList<Tuple> tuples;

    List<ColumnDescriptor> sortColumns;

    List<Boolean> ascendingOrders;

    boolean ascending = true;

    
    /**
     *
     * @param childOperation the child operation
     * @param col the name of the column used to sort the tuples. The name can
     * be prefixed by the table name (e.g. tab.col)
     * @throws Exception
     */
    public Sort(Operation childOperation, String col, boolean ascending) throws Exception {
        super(childOperation);
        setSortCriteria(new String[]{col}, new boolean[]{ascending});
    }
    
    /**
     *
     * @param childOperation the child operation
     * @param columns the name of the column used to sort the tuples. 
     * @throws Exception
     */
    public Sort(Operation childOperation, String[] columns, boolean ascending) throws Exception {
        super(childOperation);
        boolean[] ascendingOrders = new boolean[columns.length];
        Arrays.fill(ascendingOrders, ascending);
        setSortCriteria(columns, ascendingOrders);
    }

    /**
     *
     * @param childOperation the child operation
     * @param columns the names of the columns used to sort the tuples.
     * @param ascendingOrders the sorting direction of each corresponding column.
     * @throws Exception
     */
    public Sort(Operation childOperation, String[] columns, boolean[] ascendingOrders) throws Exception {
        super(childOperation);
        setSortCriteria(columns, ascendingOrders);
    }

    private void setSortCriteria(String[] columns, boolean[] ascendingOrders) throws Exception {
        if (columns == null || ascendingOrders == null || columns.length == 0) {
            throw new Exception("At least one sort column must be provided");
        }

        if (columns.length != ascendingOrders.length) {
            throw new Exception("Sort columns and sort directions must have the same size");
        }

        sortColumns = new ArrayList();
        this.ascendingOrders = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            String col = columns[i];
            ColumnDescriptor sortColumn = new ColumnDescriptor(col);
            sortColumns.add(sortColumn);
            this.ascendingOrders.add(ascendingOrders[i]);
        }

        this.ascending = this.ascendingOrders.get(0);
    }

    @Override
    public void prepare() throws Exception {
        
        //sets the index of the tuple that contains the column used to sort the tuples
        //tupleIndex = childOperation.getTupleIndex(sortColumn.getTableName());
        setSortColumnsIndexes();
        
        tuples = null;
        
        super.prepare();
        
    }
    
    private void setSortColumnsIndexes() throws Exception{
        for (ColumnDescriptor sortColumn : sortColumns) {
            //int index = childOperation.getRowIndex(sortColumn.getTableName());
            //sortColumn.setTupleIndex(index);
            childOperation.setColumnLocation(sortColumn);
        }
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples,  boolean withFilterDelegation) {
        return new SortIterator(processedTuples,  withFilterDelegation);
    }

    /**
     *
     * @return the comparator class used to sort tuples.
     */
    public Comparator<Tuple> createComparator() {
        return new TupleComparator();
    }

    /**
     * this class produces resulting tuples from the sorting over the tuples
     * that come from the child operation. The incoming tuples are materialized
     * in memory, and then sorted.
     */
    private class SortIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> it;

        /**
         * @param processedTuples the tuples that come from operations already processed.  The columns from these tuples can be used by the unprocessed operations, like for filtering. 
         * @param lookup the condition from the parent operation that needs to
         * be satisfied
         */
        public SortIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
        super(processedTuples, withFilterDelegation, getDelegatedFilters());   
           
            //build materialized collection, if one does not exist yet.
            if (tuples == null) {
                tuples = new ArrayList<>();
                try {

                    //ExternalMergeSort sorter = new ExternalMergeSort(5000, 5000);
                    //sorter.setTupleIndex(sourceTupleIndex);
                    //accesses and stores all tuples that come from the child operation
                    int tupleSize = childOperation.getTupleSize();
                    it = childOperation.lookUp(processedTuples,  false);
                    while (it.hasNext()) {
                        Tuple tuple = (Tuple) it.next();
                        tuples.add(tuple);
                        //sorter.addTuple(tuple);

                    }
                    QueryStats.MEMORY_USED+=tuples.size()*tupleSize;
                    //sort collection
                    Comparator<Tuple> comparator = createComparator();
                    Collections.sort(tuples, comparator);
                    
                    ibd.query.QueryStats.SORT_TUPLES+=tuples.size();

                    //sortedInputBucket = sorter.sort();
                } catch (Exception ex) {
                    Logger.getLogger(Sort.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //build iterator
            it = tuples.iterator();

            //it = new InputBucketWrapper(sortedInputBucket);
        }

        @Override
        protected Tuple findNextTuple() {
            //iterates over all tuples from the materialized collection.
            while (it.hasNext()) {
                Tuple tp = it.next();
                return tp;
            }
            return null;
        }

    }

    /**
     * A comparator class that defines how tuples are sorted. The tuples are
     * compared by the order of the sortColumns.
     */
    public class TupleComparator implements Comparator<Tuple> {

        @Override
        public int compare(Tuple tt1, Tuple tt2) {
            for (int i = 0; i < sortColumns.size(); i++) {
                ColumnDescriptor sortColumn = sortColumns.get(i);
                Comparable value1 = tt1.rows[sortColumn.getColumnLocation().rowIndex].getValue(sortColumn.getColumnLocation().colIndex);
                Comparable value2 = tt2.rows[sortColumn.getColumnLocation().rowIndex].getValue(sortColumn.getColumnLocation().colIndex);
                int comp = value1.compareTo(value2);
                if (!ascendingOrders.get(i)) {
                    comp = -comp;
                }
                if (comp!=0) return comp;
            }
            
            
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Sort(" + sortColumns+")";
    }

}
