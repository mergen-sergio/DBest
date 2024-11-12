/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.aggregation;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.unaryop.aggregation.AggregationType;
import ibd.query.unaryop.sort.Sort;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.column.DoubleColumn;
import ibd.table.prototype.column.FloatColumn;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.LongColumn;
import ibd.table.prototype.column.StringColumn;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static operations.unary.Group.PREFIXES;
import utils.Utils;

/**
 * This operation groups tuples and computes an aggregated value (MIN. MAX, SUM,
 * AVG, COUNT) for each group
 *
 * @author Sergio
 */
public class Aggregation extends UnaryOperation {

    //the alias identifying the tuples generated by this operation
    String alias;

    //the group by column is used to group the incoming tuples
    ColumnDescriptor groupByColumn;
    //the aggregated column is used to perform the selected aggregation
    List<AggregationType> aggregationTypes;

    //the schema of the rows returned by this operation
    Prototype prototype = null;

    /**
     *
     * @param op the operation to be connected with this unary operation
     * @param alias the alias identifying the tuples generated by this operation
     * @param groupByCol the name of the column to be used to group tuples. The
     * name can be prefixed by the table name (e.g. tab.col)
     * @param aggregateCol the name of the column to be used to aggregate values
     * from the grouped tuples. The name can be prefixed by the table name (e.g.
     * tab.col)
     * @param type the type of aggregation to be performed (AVG, COUNT, MIN,
     * MAX, SUM)
     * @param isOrdered indicates if the incoming tuples are already ordered by
     * the groupByCol column
     * @throws Exception
     */
    public Aggregation(Operation op, String alias, String groupByCol, String aggregateCol, int type, boolean isOrdered) throws Exception {
        super(op);
        this.alias = alias;
        groupByColumn = new ColumnDescriptor(groupByCol);
        aggregationTypes = new ArrayList();
        AggregationType aggType = new AggregationType(aggregateCol, type);
        aggregationTypes.add(aggType);

        //If the incoming tupled are not already sorted, an intermediary sort operation is added
        if (!isOrdered) {
            Sort cs = new Sort(childOperation, new String[]{groupByColumn.getColumnName()}, true);
            childOperation = cs;
        }
    }

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the alias identifying the tuples generated by this operation
     * @param groupByTable the name of the table to be used to group tuples
     * @param groupByCol the name of the column to be used to group tuples
     * @param groupedTable the name of the table to be used to aggregate values
     * from the grouped tuples.
     * @param groupedCol the name of the column to be used to aggregate values
     * from the grouped tuples.
     * @param type the type of aggregation to be performed (AVG, COUNT, MIN,
     * MAX, SUM)
     * @param isOrdered indicates if the incoming tuples are already ordered by
     * the groupByCol column
     * @throws Exception
     */
    public Aggregation(Operation op, String alias, String groupByTable, String groupByCol, String groupedTable, String groupedCol, int type, boolean isOrdered) throws Exception {
        super(op);
        this.alias = alias;
        groupByColumn = new ColumnDescriptor(groupByTable, groupByCol);

        aggregationTypes = new ArrayList();
        AggregationType aggType = new AggregationType(groupedTable, groupedCol, type);
        aggregationTypes.add(aggType);

        //If the incoming tupled are not already sorted, an intermediary sort operation is added
        if (!isOrdered) {
            Sort cs = new Sort(childOperation, new String[]{groupByColumn.getColumnName()}, true);
            childOperation = cs;
        }
    }

    public Aggregation(Operation op, String alias, String groupByTable, String groupByCol, List<AggregationType> aggregationOperations, boolean isOrdered) throws Exception {
        super(op);
        this.alias = alias;
        groupByColumn = new ColumnDescriptor(groupByTable, groupByCol);
        this.aggregationTypes = aggregationOperations;

        //If the incoming tupled are not already sorted, an intermediary sort operation is added
        if (!isOrdered) {
            Sort cs = new Sort(childOperation, new String[]{groupByColumn.getColumnName()}, true);
            childOperation = cs;
        }

    }

    public Aggregation(Operation op, String alias, String groupByCol, List<AggregationType> aggregationOperations, boolean isOrdered) throws Exception {
        super(op);
        this.alias = alias;
        groupByColumn = new ColumnDescriptor(groupByCol);
        this.aggregationTypes = aggregationOperations;

        //If the incoming tupled are not already sorted, an intermediary sort operation is added
        if (!isOrdered) {
            Sort cs = new Sort(childOperation, new String[]{groupByColumn.getColumnName()}, true);
            childOperation = cs;
        }

    }

    private Column createColumn(ColumnDescriptor colDesc, String colName) throws Exception {
        //ColumnLocation location = colDesc.getColumnLocation();
        //ReferedDataSource[] childSources = childOperation.getDataSources();
        ReferedDataSource dataSource = childOperation.getDataSource(colDesc.getTableName());
        Column col = dataSource.prototype.getColumn(colDesc.getColumnName());

        if (colName==null)
            colName = colDesc.getColumnName();
        //Column col = childSources[location.rowIndex].prototype.getColumn(location.colIndex); 
        Column newCol = null;
        switch (col.getType()) {
            case Column.INTEGER_TYPE ->
                newCol = new IntegerColumn(colName);
            case Column.STRING_TYPE ->
                newCol = new StringColumn(colName);
            case Column.LONG_TYPE ->
                newCol = new LongColumn(colName);
            case Column.DOUBLE_TYPE ->
                newCol = new DoubleColumn(colName);
            case Column.FLOAT_TYPE ->
                newCol = new FloatColumn(colName);
        }
        return newCol;
    }

    private void createPrototype() throws Exception {

        prototype = new Prototype();
        prototype.addColumn(createColumn(groupByColumn, null));
        for (AggregationType aggregationType : aggregationTypes) {
            if (aggregationType.type == AggregationType.AVG) {
                prototype.addColumn(new DoubleColumn(aggregationType.aggregateColumnName));
                //prototype.addColumn(new DoubleColumn(aggregateColumn.getColumnName()));
            } else if (aggregationType.type != AggregationType.FIRST && aggregationType.type != AggregationType.LAST){
                prototype.addColumn(new IntegerColumn(aggregationType.aggregateColumnName));
                //prototype.addColumn(new IntegerColumn(aggregateColumn.getColumnName()));
            }
            else prototype.addColumn(createColumn(aggregationType.aggregateColumn,aggregationType.aggregateColumnName));
        }

        prototype.validateColumns();
    }
    
    @Override
    public void prepare() throws Exception {

        super.prepare();

        //uses the table's names to set the tuple indexes 
        childOperation.setColumnLocation(groupByColumn);
        for (AggregationType aggregationType : aggregationTypes) {
            childOperation.setColumnLocation(aggregationType.aggregateColumn);
        }

        //groupByTupleIndex = childOperation.getRowIndex(groupByColumn.getTableName());
        //aggregatedTupleIndex = childOperation.getRowIndex(aggregateColumn.getTableName());
    }

    /**
     * {@inheritDoc }
     * An aggregation defines its own schema. The schema of the rows coming into
     * an aggregation are combined into one, according to the type of
     * aggregation needed.
     *
     * @throws Exception
     */
    @Override
    public void setDataSourcesInfo() throws Exception {
        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;

        childOperation.setDataSourcesInfo();

        createPrototype();
        dataSources[0].prototype = prototype;
    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();

        List<String> list = new ArrayList<>();
        list.add(groupByColumn.getColumnName());
        for (AggregationType aggregationType : aggregationTypes) {
            list.add(aggregationType.aggregateColumnName);
        }
        map.put(alias, list);
        return map;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new AggregationIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        String aggregation = "";
        for (AggregationType aggregationType : aggregationTypes) {
            aggregation += ",";
            aggregation += aggregationType.toString();
        }
        return "Group by(" + groupByColumn.toString() + ")" + aggregation;
    }

    /**
     * this class produces resulting tuples from an aggregation over the child
     * operation
     */
    private class AggregationIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        Comparable prevGroupByValue;
        List<Comparable> groupedValues[];

        public AggregationIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            groupedValues = new List[aggregationTypes.size()];
            for (int i = 0; i < groupedValues.length; i++) {
                groupedValues[i] = new ArrayList();
            }
            tuples = childOperation.lookUp(processedTuples, false);//returns all tuples from the child operation 
        }

        private Comparable getValue(Tuple tp, ColumnDescriptor col) {
            return tp.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnName());
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
//                //a tuple must satisfy the lookup filter 
//                if (!lookup.match(tp)) {
//                    continue;
//                }
                Comparable groupByValue = getValue(tp, groupByColumn);
                //Comparable aggregatedValue = getValue(tp, aggregateColumn);

                if (prevGroupByValue == null) {
                    prevGroupByValue = groupByValue;
                    for (int i = 0; i < aggregationTypes.size(); i++) {
                        AggregationType aggregationType = aggregationTypes.get(i);
                        Comparable aggregatedValue = getValue(tp, aggregationType.aggregateColumn);
                        if (aggregatedValue!=null)
                            groupedValues[i].add(aggregatedValue);
                    }

                } else if ((prevGroupByValue.equals(groupByValue))) {
                    for (int i = 0; i < aggregationTypes.size(); i++) {
                        AggregationType aggregationType = aggregationTypes.get(i);
                        Comparable aggregatedValue = getValue(tp, aggregationType.aggregateColumn);
                        if (aggregatedValue!=null)
                            groupedValues[i].add(aggregatedValue);
                    }
                } else //if ((!prevGroupByValue.equals(groupByValue))) 
                {
                    Tuple tuple = new Tuple();
                    //BasicDataRow dataRow = new BasicDataRow();
                    LinkedDataRow dataRow = new LinkedDataRow(prototype, false);
                    dataRow.setValue(0, prevGroupByValue);
                    aggregate(dataRow, aggregationTypes, groupedValues);
                    //dataRow.setMetadata(prototype);
                    tuple.setSingleSourceRow(alias, dataRow);

                    prevGroupByValue = groupByValue;

                    for (int i = 0; i < aggregationTypes.size(); i++) {
                        AggregationType aggregationType = aggregationTypes.get(i);
                        Comparable aggregatedValue = getValue(tp, aggregationType.aggregateColumn);
                        groupedValues[i].clear();
                        if (aggregatedValue!=null)
                            groupedValues[i].add(aggregatedValue);
                    }
                    
                    if (!lookup.match(tuple)) {
                    continue;
                }
                    return tuple;
                }

            }
            if (prevGroupByValue != null) {
                Tuple tuple = new Tuple();
                LinkedDataRow dataRow = new LinkedDataRow(prototype, false);
                dataRow.setValue(0, prevGroupByValue);
                aggregate(dataRow, aggregationTypes, groupedValues);
                //dataRow.setMetadata(prototype);
                tuple.setSingleSourceRow(alias, dataRow);
                prevGroupByValue = null;
                if (lookup.match(tuple)) 
                    return tuple;
            }

            return null;
        }

        private void aggregate(LinkedDataRow row, List<AggregationType> aggregationTypes, List<Comparable> list[]) {
            for (int i = 0; i < list.length; i++) {

                switch (aggregationTypes.get(i).type) {
                    case AggregationType.AVG -> {
                        row.setValue(i + 1, avg(list[i]));
                    }
                    case AggregationType.MAX -> {
                        row.setValue(i + 1, max(list[i]));
                    }
                    case AggregationType.MIN -> {
                        row.setValue(i + 1, min(list[i]));
                    }
                    case AggregationType.COUNT -> {
                        row.setValue(i + 1, count(list[i]));
                    }
                    case AggregationType.SUM -> {
                        row.setValue(i + 1, sum(list[i]));
                    }
                    case AggregationType.FIRST -> {
                        row.setValue(i + 1, first(list[i]));
                    }
                    case AggregationType.LAST -> {
                        row.setValue(i + 1, last(list[i]));
                    }
                }
            }

        }

        private Double avg(List<Comparable> list) {

            if (list.isEmpty()) {
                return 0.;
            }
            int sum = 0;
            for (Comparable integer : list) {
                sum += (Integer)integer;
            }
            return Double.valueOf(sum / list.size());
        }

        private Integer sum(List<Comparable> list) {

            int sum = 0;
            for (Comparable integer : list) {
                sum += (Integer)integer;
            }
            return sum;
        }

        private Integer min(List<Comparable> list) {

            if (list.isEmpty()) {
                return 0;
            }
            int min = Integer.MAX_VALUE;
            for (Comparable integer : list) {
                if ((Integer)integer < min) {
                    min = (Integer)integer;
                }
            }
            return min;
        }

        private Integer max(List<Comparable> list) {

            if (list.isEmpty()) {
                return 0;
            }
            int max = Integer.MIN_VALUE;
            for (Comparable integer : list) {
                if ((Integer)integer > max) {
                    max = (Integer)integer;
                }
            }
            return max;
        }

        private Integer count(List<Comparable> list) {
            return list.size();
        }

        private Comparable first(List<Comparable> list) {

            if (list.isEmpty()) {
                return 0;
            }
            return list.get(0);
        }

        private Comparable last(List<Comparable> list) {

            if (list.isEmpty()) {
                return 0;
            }
            return list.get(list.size() - 1);
        }

    }

}
