package database;

import controllers.ConstantController;
import entities.Column;
import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

import java.io.File;
import java.util.*;

public class TuplesExtractor {

    private TuplesExtractor() {

    }

    public static Map<Integer, Map<String, String>> getAllRowsMap(Operation operator, boolean sourceAndName) {
        Map<Integer, Map<String, String>> rows = new HashMap<>();

        try {
            operator.open();

            Set<String> getPossibleKeys = getPossibleKeys(operator, sourceAndName);
            Map<String, String> row;
            row = getRow(operator, sourceAndName, getPossibleKeys);
            int i = 0;
            while (row != null) {
                rows.put(i++, row);
                row = getRow(operator, sourceAndName, getPossibleKeys);
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
            Set<String> getPossibleKeys = getPossibleKeys(operator, sourceAndName);
            
            Map<String, String> row;
            row = getRow(operator, sourceAndName, getPossibleKeys);
            int i = 1;
            while (row != null && i++ <= amount) {
                rows.add(row);
                row = getRow(operator, sourceAndName, getPossibleKeys);
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
            Set<String> getPossibleKeys = getPossibleKeys(operator, sourceAndName);
            
            Map<String, String> row;
            row = getRow(operator, sourceAndName,getPossibleKeys);

            while (row != null) {
                rows.add(row);
                row = getRow(operator, sourceAndName, getPossibleKeys);
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

        Set<String> getPossibleKeys = getPossibleKeys(operator, sourceAndName);
        List<Map<String, String>> rows = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Map<String, String> row = getRow(operator, sourceAndName, getPossibleKeys);
            if (row == null) {
                return rows;
            }

            rows.add(row);
        }

        return rows;

    }
    
    public static Set<String> getPossibleKeys(Operation operator, boolean sourceAndName) {
    Set<String> possibleKeys = new HashSet<>();
    for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
            possibleKeys.addAll(content
                    .getValue()
                    .stream()
                    .map(columnName -> sourceAndName ? entities.Column.composeSourceAndName(content.getKey(), columnName) : columnName)
                    .toList()
            );
        }
    return possibleKeys;
    }

    public static Map<String, String> getRow(Operation operator, boolean sourceAndName, Set<String> possibleKeys) {
        if (operator == null) {
            return null;
        }


        Map<String, String> row = new TreeMap<>();

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
