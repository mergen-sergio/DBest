package operations;

import com.mxgraph.model.mxCell;

import entities.cells.OperationCell;


import java.util.List;

import static entities.utils.cells.CellUtils.changeCellName;

public class Operation {

	private Operation() {

	}

	public static void operationSetter(OperationCell cell, String name, List<String> arguments, ibd.query.Operation operator) {
		mxCell jCell = cell.getJCell();

		cell.setOperator(operator);
		cell.setName(name);
		cell.setArguments(arguments);
		cell.removeError();

		changeCellName(jCell, name);

	}
}
