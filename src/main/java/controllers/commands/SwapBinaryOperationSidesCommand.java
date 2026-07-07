package controllers.commands;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellRepository;
import enums.OperationArity;
import gui.frames.forms.operations.JoinForm;
import gui.frames.main.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SwapBinaryOperationSidesCommand extends BaseUndoableRedoableCommand {

    private final mxCell operationJCell;
    private OperationCell operationCell;

    public SwapBinaryOperationSidesCommand(mxCell operationJCell) {
        this.operationJCell = operationJCell;
    }

    @Override
    public void execute() {
        if (this.operationCell == null) {
            Optional<Cell> optionalCell = CellRepository.getActiveCell(this.operationJCell);
            if (optionalCell.isEmpty() || !(optionalCell.get() instanceof OperationCell operationCell)) {
                return;
            }

            this.operationCell = operationCell;
        }

        swapSides();
    }

    @Override
    public void undo() {
        swapSides();
    }

    @Override
    public void redo() {
        swapSides();
    }

    private void swapSides() {
        if (this.operationCell == null
                || this.operationCell.getArity() != OperationArity.BINARY
                || this.operationCell.getParents().size() != 2) {
            return;
        }

        Cell leftParent = this.operationCell.getLeftParent();
        Cell rightParent = this.operationCell.getRightParent();

        if (leftParent == null || rightParent == null) {
            return;
        }

        Cell newLeftParent = rightParent;
        Cell newRightParent = leftParent;

        if (isJoinOperation()) {
            this.operationCell.setArguments(orientJoinArguments(
                    this.operationCell.getArguments(),
                    newLeftParent,
                    newRightParent
            ));
        }

        this.operationCell.setParents(List.of(newLeftParent, newRightParent));
        TreeUtils.recalculateContent(this.operationCell);
        MainFrame.getGraph().refresh();
    }

    private boolean isJoinOperation() {
        return this.operationCell.getType() != null
                && this.operationCell.getType().form == JoinForm.class;
    }

    private List<String> orientJoinArguments(List<String> arguments, Cell leftParent, Cell rightParent) {
        List<String> orientedArguments = new ArrayList<>();

        for (String argument : arguments) {
            int separatorIndex = argument.indexOf("=");

            if (separatorIndex < 0) {
                orientedArguments.add(argument);
                continue;
            }

            String leftArgument = argument.substring(0, separatorIndex).trim();
            String rightArgument = argument.substring(separatorIndex + 1).trim();

            if (belongsToParent(leftParent, leftArgument) && belongsToParent(rightParent, rightArgument)) {
                orientedArguments.add(leftArgument + "=" + rightArgument);
            } else {
                orientedArguments.add(rightArgument + "=" + leftArgument);
            }
        }

        return orientedArguments;
    }

    private boolean belongsToParent(Cell parent, String argument) {
        String sourceName = extractSourceName(argument);

        if (sourceName == null) {
            return false;
        }

        return parent.getColumns().stream().anyMatch(column -> column.SOURCE.equals(sourceName));
    }

    private String extractSourceName(String argument) {
        int separatorIndex = argument.indexOf(".");

        if (separatorIndex < 0) {
            return null;
        }

        return argument.substring(0, separatorIndex).trim();
    }
}
