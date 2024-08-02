package database;

import controllers.ConstantController;
import entities.Column;
import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

import java.util.*;

public class TuplesExtractor {

    private TuplesExtractor() {

    }

    public static Map<Integer, Map<String, String>> getAllRowsMap(Operation operator, boolean sourceAndName) {
        Map<Integer, Map<String, String>> rows = new HashMap<>();

        try {
            operator.open();

            Map<String, String> row;

            row = getRow(operator, sourceAndName);
            int i = 0;
            while (row != null) {
                rows.put(i++, row);
                row = getRow(operator, sourceAndName);
            }

            operator.close();

        } catch (Exception ex) {
        }
        return rows;
    }

    public static List<Map<String, String>> getRows(Operation operator, int amount, boolean sourceAndName) {
        List<Map<String, String>> rows = new ArrayList<>();
        try {
            operator.open();

            Map<String, String> row;

            row = getRow(operator, sourceAndName);
            int i = 1;
            while (row != null && i++ <= amount) {
                rows.add(row);
                row = getRow(operator, sourceAndName);
            }

            operator.close();
        } catch (Exception ex) {
        }
        return rows;
    }

    public static List<Map<String, String>> getAllRowsList(Operation operator, boolean sourceAndName) {

        List<Map<String, String>> rows = new ArrayList<>();

        try {
            operator.open();

            Map<String, String> row;

            row = getRow(operator, sourceAndName);

            while (row != null) {
                rows.add(row);
                row = getRow(operator, sourceAndName);
            }

            operator.close();
        } catch (Exception ex) {
        }

        return rows;
    }

    public static List<Map<String, String>> getRows(Operation operator, boolean sourceAndName, int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException();
        }

        List<Map<String, String>> rows = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Map<String, String> row = getRow(operator, sourceAndName);
            if (row == null) {
                return rows;
            }

            rows.add(row);
        }

        return rows;

    }

//    public static Map<String, String> getRow(Operator operator, boolean sourceAndName) {
//        if (operator == null) return null;
//
//        Set<String> possibleKeys = new HashSet<>();
//
//        Map<String, String> row = new TreeMap<>();
//
//        for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
//            possibleKeys.addAll(content
//                .getValue()
//                .stream()
//                .map(columnName -> sourceAndName ? entities.Column.composeSourceAndName(content.getKey(), columnName) : columnName)
//                .toList()
//            );
//        }
//
//        if (operator.hasNext()) {
//            sgbd.prototype.query.Tuple tuple = operator.next();
//
//            for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
//                for (String columnName : content.getValue()) {
//                    RowData rowData = tuple.getContent(content.getKey());
//                    String sourceAndColumn = sourceAndName ? Column.composeSourceAndName(content.getKey(), columnName) : columnName;
//
//                    switch (Utils.getColumnDataType(tuple, content.getKey(), columnName)) {
//                        case INTEGER ->
//                            row.put(sourceAndColumn, Objects.toString(rowData.getInt(columnName), ConstantController.NULL));
//                        case LONG ->
//                            row.put(sourceAndColumn, Objects.toString(rowData.getLong(columnName), ConstantController.NULL));
//                        case FLOAT ->
//                            row.put(sourceAndColumn, Objects.toString(rowData.getFloat(columnName), ConstantController.NULL));
//                        case DOUBLE ->
//                            row.put(sourceAndColumn, Objects.toString(rowData.getDouble(columnName), ConstantController.NULL));
//                        case BOOLEAN ->
//                            row.put(sourceAndColumn, Objects.toString(rowData.getBoolean(columnName), ConstantController.NULL));
//                        case STRING, NONE, CHARACTER ->
//                            row.put(sourceAndColumn, Objects.toString(rowData.getString(columnName), ConstantController.NULL));
//                    }
//                }
//            }
//        } else {
//            return null;
//        }
//
//        for (String key : possibleKeys) {
//            if (!row.containsKey(key)) {
//                row.put(key, ConstantController.NULL);
//            }
//        }
//
//        return row;
//    }
    public static Map<String, String> getRow(Operation operator, boolean sourceAndName) {
        if (operator == null) {
            return null;
        }

        Set<String> possibleKeys = new HashSet<>();

        Map<String, String> row = new TreeMap<>();

        for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
            possibleKeys.addAll(content
                    .getValue()
                    .stream()
                    .map(columnName -> sourceAndName ? entities.Column.composeSourceAndName(content.getKey(), columnName) : columnName)
                    .toList()
            );
        }

        if (operator.hasNext()) {
            Tuple tuple = operator.next();
            int rowIndex = 0;
            ReferedDataSource[] dataSources = null;
            try {
                dataSources = operator.getDataSources();
            } catch (Exception ex) {
            }
            for (LinkedDataRow row1 : tuple.rows) {

                String alias = dataSources[rowIndex].alias;
                int colIndex = 0;
                for (ibd.table.prototype.column.Column column : row1.getPrototype().getColumns()) {
                    String columnName = column.getName();
                    String sourceAndColumn = sourceAndName ? Column.composeSourceAndName(alias, columnName) : columnName;
                    boolean hasValue = row1.hasValue(columnName);
                    if (hasValue) {
                        switch (column.getType()) {
                            case ibd.table.prototype.column.Column.INTEGER_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getInt(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.LONG_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getLong(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.FLOAT_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getFloat(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.DOUBLE_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getDouble(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.BOOLEAN_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getBoolean(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.STRING_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getString(columnName), ConstantController.NULL));
                            default ->
                                row.put(sourceAndColumn, Objects.toString(row1.getString(columnName), ConstantController.NULL));

                        }
                    }
                    colIndex++;
                }
                rowIndex++;
            }

        } else {
            return null;
        }

        for (String key : possibleKeys) {
            if (!row.containsKey(key)) {
                row.put(key, ConstantController.NULL);
            }
        }

        return row;
    }
}
