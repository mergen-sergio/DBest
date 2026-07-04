package controllers.commands;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellRepository;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.JoinForm;
import gui.frames.main.MainFrame;

import java.util.Optional;

public class ReplaceJoinOperationCommand extends BaseUndoableRedoableCommand {

    private final mxCell operationJCell;
    private final OperationType newType;
    private OperationCell operationCell;
    private OperationType oldType;

    public ReplaceJoinOperationCommand(mxCell operationJCell, OperationType newType) {
        this.operationJCell = operationJCell;
        this.newType = newType;
    }

    @Override
    public void execute() {
        if (this.operationCell == null) {
            Optional<Cell> optionalCell = CellRepository.getActiveCell(this.operationJCell);
            if (optionalCell.isEmpty() || !(optionalCell.get() instanceof OperationCell operationCell)) {
                return;
            }

            this.operationCell = operationCell;
            this.oldType = operationCell.getType();
        }

        replaceWith(this.newType);
    }

    @Override
    public void undo() {
        replaceWith(this.oldType);
    }

    @Override
    public void redo() {
        replaceWith(this.newType);
    }

    private void replaceWith(OperationType type) {
        if (this.operationCell == null
                || !isJoinType(this.operationCell.getType())
                || !isJoinType(type)
                || this.operationCell.getType() == type) {
            return;
        }

        this.operationCell.replaceType(type);
        TreeUtils.recalculateContent(this.operationCell);
        MainFrame.getGraph().refresh();
    }

    private boolean isJoinType(OperationType type) {
        return type != null
                && type.arity == OperationArity.BINARY
                && type.form == JoinForm.class;
    }
}
