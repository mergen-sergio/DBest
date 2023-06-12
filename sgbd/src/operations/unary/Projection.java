package operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;
import util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Projection implements IOperator {

	public Projection() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		ErrorMessage error = null;

		try {
			
			error = ErrorMessage.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = ErrorMessage.NO_ONE_PARENT;
			OperationErrorVerifier.oneParent(cell);
			
			error = ErrorMessage.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);
			
			error = ErrorMessage.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);
			
			error = ErrorMessage.PARENT_WITHOUT_COLUMN;
			OperationErrorVerifier.parentContainsColumns(Column.sourceAndNameTogether(cell.getParents().get(0).getColumns()), arguments);
			error = null;

		} catch (TreeException e) {

			cell.setError(error);

		}
		
		if(error != null) return;

		Cell parentCell = cell.getParents().get(0);

		List<Column> aux = parentCell.getColumns().stream().filter(x ->
			!Utils.listElementStartsAndEndsWith(arguments, x.getSource(), x.getName())).toList();

		Operator operator = parentCell.getOperator();

		for (Column c : aux)
			operator = new FilterColumnsOperator(operator, c.getSource(), List.of(c.getName()));

		Operation.operationSetter(cell, "π  " + arguments.toString(), arguments, operator);

	}

}