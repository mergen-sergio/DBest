package operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import ibd.query.unaryop.aggregation.AggregationType;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Group implements IOperator {

    public static final List<String> PREFIXES = List.of("MIN:", "MAX:", "AVG:", "COUNT:", "FIRST:","LAST:","SUM:");

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {
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
                cell.getParents().get(0).getColumnSourcesAndNames(), arguments.stream().limit(1).toList()
            );

            OperationErrorVerifier.parentContainsColumns(
                cell.getParents().get(0).getColumnSourcesAndNames(),
                arguments
                    .stream()
                    .map(x -> Utils.replaceIfStartsWithIgnoreCase(x, PREFIXES, ""))
                    .toList()
                    .subList(1, arguments.size())
            );

            errorType = OperationErrorType.NO_PREFIX;
            OperationErrorVerifier.everyoneHavePrefix(arguments.subList(1, arguments.size()), PREFIXES);

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        List<String> fixedArguments = new ArrayList<>();

        fixedArguments.add(Column.composeSourceAndName(parentCell.getSourceNameByColumnName(arguments.get(0)), arguments.get(0)));

        for (String argument : arguments.subList(1, arguments.size())) {
            String fixedArgument = argument.substring(0, Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length())
                + Column.composeSourceAndName(parentCell.getSourceNameByColumnName(argument.substring(Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length())), argument.substring(Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length())
            );

            fixedArguments.add(fixedArgument);
        }

        String groupBy = fixedArguments.get(0);

        List<AggregationType> aggregations = new ArrayList<>();

        for (String argument : fixedArguments.subList(1, arguments.size())) {
            String column = argument.substring(Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length());
            String sourceName = Column.removeName(column);
            String columnName = Column.removeSource(column);

            if (Utils.startsWithIgnoreCase(argument, "MAX:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.MAX));
            } else if (Utils.startsWithIgnoreCase(argument, "MIN:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.MIN));
            } else if (Utils.startsWithIgnoreCase(argument, "AVG:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.AVG));
            } else if (Utils.startsWithIgnoreCase(argument, "COUNT:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT));
            } else if (Utils.startsWithIgnoreCase(argument, "FIRST:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.FIRST));
            } else if (Utils.startsWithIgnoreCase(argument, "LAST:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.LAST));
            } 
            else if (Utils.startsWithIgnoreCase(argument, "SUM:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.SUM));
            }
        }

        ibd.query.Operation operator = parentCell.getOperator();
        //ibd.query.Operation readyOperator = new GroupOperator(operator, Column.removeName(groupBy), Column.removeSource(groupBy), aggregations);
        ibd.query.Operation readyOperator = null;
        try {
            //readyOperator = new ibd.query.unaryop.Aggregation(operator, "aggregate", groupBy, aggregateCol, aggregateType, false);
            readyOperator = new ibd.query.unaryop.aggregation.Aggregation(operator, "aggregate", groupBy, aggregations, false);
        } catch (Exception ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
        }

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }
}
