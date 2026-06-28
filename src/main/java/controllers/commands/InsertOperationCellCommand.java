package controllers.commands;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Action.CurrentAction;
import entities.Action.CreateOperationCellAction;
import entities.cells.OperationCell;
import entities.utils.CoordinatesUtils;
import entities.utils.cells.CellRepository;
import entities.utils.cells.CellUtils;
import enums.CellType;
import enums.OperationType;
import gui.frames.main.MainFrame;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.unaryop.Reference;
import ibd.query.unaryop.filter.Condition;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Records the placement of a new operation node on the canvas.
 * Edge creation is handled separately by ConnectNodesCommand.
 */
public class InsertOperationCellCommand extends BaseUndoableRedoableCommand {

    private final MouseEvent mouseEvent;
    private final mxCell ghostCell;
    private final AtomicReference<CurrentAction> currentActionReference;
    private final OperationType operationType;
    private final mxCell parentToAutoConnect;

    private mxCell createdJCell;
    private OperationCell createdOperationCell;
    private ConnectNodesCommand autoConnectCommand;

    public InsertOperationCellCommand(
        MouseEvent mouseEvent,
        mxCell ghostCell,
        AtomicReference<CurrentAction> currentActionReference
    ) {
        CreateOperationCellAction action = (CreateOperationCellAction) currentActionReference.get();
        this.mouseEvent = mouseEvent;
        this.ghostCell = ghostCell;
        this.currentActionReference = currentActionReference;
        this.operationType = action.getOperationType();
        this.parentToAutoConnect = action.hasParent() ? action.getParent() : null;
    }

    @Override
    public void execute() {
        // Remove the ghost (preview) cell
        if (this.ghostCell != null) {
            MainFrame.getGraph().removeCells(new Object[]{this.ghostCell}, true);
        }

        var canvasCoords = CoordinatesUtils.transformScreenToCanvasCoordinates(this.mouseEvent);
        int width = CellUtils.getCellWidth(this.operationType.getFormattedDisplayName());
        int height = ConstantController.OPERATION_CELL_HEIGHT;

        this.createdJCell = (mxCell) MainFrame.getGraph().insertVertex(
            MainFrame.getGraph().getDefaultParent(), null,
            this.operationType.getFormattedDisplayName(),
            canvasCoords.x() - width / 2.0,
            canvasCoords.y() - height / 2.0,
            width, height, CellType.OPERATION.id
        );

        this.createdOperationCell = new OperationCell(this.createdJCell, this.operationType);

        // Special initialization for operations with default operators
        if (this.operationType == OperationType.CONDITION) {
            try {
                this.createdOperationCell.setOperator(new Condition(new NoLookupFilter()));
            } catch (Exception ignored) {}
        } else if (this.operationType == OperationType.REFERENCE) {
            try {
                this.createdOperationCell.setOperator(
                    new Reference(new ArrayList<String>().toArray(new String[0]))
                );
            } catch (Exception ignored) {}
        }

        this.currentActionReference.set(ConstantController.NONE_ACTION);

        if (this.parentToAutoConnect != null) {
            this.autoConnectCommand = new ConnectNodesCommand(
                this.parentToAutoConnect, this.createdJCell, null
            );
            this.autoConnectCommand.execute();
        }
    }

    @Override
    public void undo() {
        if (this.autoConnectCommand != null) {
            this.autoConnectCommand.undo();
        }

        if (this.createdJCell == null) return;
        CellRepository.removeCell(this.createdJCell);
        MainFrame.getGraph().removeCells(new Object[]{this.createdJCell}, false);
        MainFrame.getGraph().refresh();
    }

    @Override
    public void redo() {
        if (this.createdJCell == null || this.createdOperationCell == null) return;
        MainFrame.getGraph().getModel().beginUpdate();
        try {
            MainFrame.getGraph().addCell(this.createdJCell);
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
        }
        CellRepository.addCell(this.createdJCell, this.createdOperationCell);
        MainFrame.getGraph().refresh();

        if (this.autoConnectCommand != null) {
            this.autoConnectCommand.redo();
        }
    }
}
