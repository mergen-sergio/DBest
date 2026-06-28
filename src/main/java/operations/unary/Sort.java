package operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Sort implements IOperator {

    public static final List<String> PREFIXES = List.of("ASC:", "DESC:");

    public static boolean isAscending(String argument) {
        return !Utils.startsWithIgnoreCase(argument, "DESC:");
    }

    public static String getPrefix(boolean ascending) {
        return ascending ? "ASC:" : "DESC:";
    }

    public static String removeOrderPrefix(String argument) {
        return Utils.replaceIfStartsWithIgnoreCase(argument, PREFIXES, "");
    }

    @Override
    @SuppressWarnings(value = "deprecation")
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            errorType = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            errorType = OperationErrorType.EMPTY_ARGUMENT;
            OperationErrorVerifier.noEmptyArgument(arguments);

            errorType = OperationErrorType.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(
                cell.getParents().get(0).getColumnSourcesAndNames(),
                arguments
                    .stream()
                    .map(Sort::removeOrderPrefix)
                    .toList()
            );

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        ibd.query.Operation operator = parentCell.getOperator();

        List<String> fixedArguments = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        boolean[] ascendingOrders = new boolean[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            boolean isAscendingOrder = isAscending(argument);
            String column = removeOrderPrefix(argument);

            boolean hasSource = Column.hasSource(column);
            String sourceName = hasSource ? Column.removeName(column) : parentCell.getSourceNameByColumnName(column);
            String columnName = hasSource ? Column.removeSource(column) : column;
            String sourceAndName = Column.composeSourceAndName(sourceName, columnName);

            ascendingOrders[i] = isAscendingOrder;
            columns.add(sourceAndName);
            fixedArguments.add(getPrefix(isAscendingOrder) + sourceAndName);
        }

        ibd.query.Operation readyOperator = null;
        try {
            readyOperator = new ibd.query.unaryop.sort.Sort(
                operator,
                columns.toArray(new String[0]),
                ascendingOrders
            );
        } catch (Exception ex) {
        }

        String operationName = String.format("%s %s", cell.getType().symbol, fixedArguments);

        Operation.operationSetter(cell, operationName, fixedArguments, readyOperator);
    }
}
