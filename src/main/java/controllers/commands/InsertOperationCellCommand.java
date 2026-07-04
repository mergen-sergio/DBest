package controllers.commands;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import controllers.ConstantController;
import entities.Action.CurrentAction;
import entities.Action.CreateOperationCellAction;
import entities.Coordinates;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
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
    private final Coordinates insertionCoordinates;

    private mxCell createdJCell;
    private OperationCell createdOperationCell;
    private ConnectNodesCommand autoConnectCommand;
    private Cell parentCell;
    private OperationCell childToRewire;
    private mxCell originalChildEdge;
    private mxCell insertedChildEdge;
    private String originalChildEdgeLabel = "";
    private boolean insertedBetween;

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
        this.insertionCoordinates = null;
    }

    public InsertOperationCellCommand(
        Coordinates insertionCoordinates,
        mxCell ghostCell,
        AtomicReference<CurrentAction> currentActionReference
    ) {
        CreateOperationCellAction action = (CreateOperationCellAction) currentActionReference.get();
        this.mouseEvent = null;
        this.ghostCell = ghostCell;
        this.currentActionReference = currentActionReference;
        this.operationType = action.getOperationType();
        this.parentToAutoConnect = action.hasParent() ? action.getParent() : null;
        this.insertionCoordinates = insertionCoordinates;
    }

    @Override
    public void execute() {
        // Remove the ghost (preview) cell
        if (this.ghostCell != null) {
            MainFrame.getGraph().removeCells(new Object[]{this.ghostCell}, true);
        }

        Coordinates canvasCoords = this.insertionCoordinates != null
            ? this.insertionCoordinates
            : CoordinatesUtils.transformScreenToCanvasCoordinates(this.mouseEvent);
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
            captureExistingChildConnection();

            this.autoConnectCommand = new ConnectNodesCommand(
                this.parentToAutoConnect, this.createdJCell, null
            );
            this.autoConnectCommand.execute();

            insertBetweenExistingConnection();
        }
    }

    @Override
    public void undo() {
        if (this.autoConnectCommand != null) {
            this.autoConnectCommand.undo();
        }

        restoreExistingChildConnection();

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

        insertBetweenExistingConnection();
    }

    private void captureExistingChildConnection() {
        this.parentCell = CellRepository.getActiveCell(this.parentToAutoConnect).orElse(null);
        if (this.parentCell == null || !this.parentCell.hasChild()) {
            return;
        }

        this.childToRewire = this.parentCell.getChild();
        this.originalChildEdge = findEdge(this.parentToAutoConnect, this.childToRewire.getJCell());
        if (this.originalChildEdge != null && this.originalChildEdge.getValue() instanceof String label) {
            this.originalChildEdgeLabel = label;
        }
    }

    private void insertBetweenExistingConnection() {
        if (this.parentCell == null || this.childToRewire == null || this.createdOperationCell == null) {
            return;
        }

        removeOriginalChildEdge();

        if (!this.childToRewire.replaceParent(this.parentCell, this.createdOperationCell)) {
            return;
        }

        this.insertedChildEdge = (mxCell) MainFrame.getGraph().insertEdge(
            MainFrame.getGraph().getDefaultParent(), null, this.originalChildEdgeLabel,
            this.createdJCell, this.childToRewire.getJCell()
        );

        this.insertedBetween = true;
        TreeUtils.updateTreesAboveAndBelow(this.createdOperationCell.getParents(), this.childToRewire);
        TreeUtils.recalculateContent(this.createdOperationCell);
        MainFrame.getGraph().refresh();
    }

    private void restoreExistingChildConnection() {
        if (!this.insertedBetween || this.parentCell == null || this.childToRewire == null) {
            return;
        }

        removeInsertedChildEdge();

        if (this.childToRewire.replaceParent(this.createdOperationCell, this.parentCell)) {
            restoreOriginalChildEdge();
            TreeUtils.updateTreesAboveAndBelow(this.childToRewire.getParents(), this.childToRewire);
            TreeUtils.recalculateContent(this.childToRewire);
            MainFrame.getGraph().refresh();
        }

        this.insertedBetween = false;
    }

    private mxCell findEdge(mxCell source, mxCell target) {
        Object[] edges = MainFrame.getGraph().getEdgesBetween(source, target);
        for (Object edge : edges) {
            if (edge instanceof mxCell edgeCell
                    && edgeCell.getSource() == source
                    && edgeCell.getTarget() == target) {
                return edgeCell;
            }
        }
        return null;
    }

    private void removeOriginalChildEdge() {
        if (this.originalChildEdge == null || !MainFrame.getGraph().getModel().contains(this.originalChildEdge)) {
            return;
        }

        removeEdge(this.originalChildEdge);
    }

    private void removeInsertedChildEdge() {
        if (this.insertedChildEdge == null || !MainFrame.getGraph().getModel().contains(this.insertedChildEdge)) {
            return;
        }

        removeEdge(this.insertedChildEdge);
    }

    private void removeEdge(mxCell edge) {
        mxGraph graph = MainFrame.getGraph();
        graph.getModel().beginUpdate();
        try {
            graph.getModel().remove(edge);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private void restoreOriginalChildEdge() {
        mxGraph graph = MainFrame.getGraph();
        graph.getModel().beginUpdate();
        try {
            if (this.originalChildEdge != null) {
                graph.getModel().add(
                    graph.getDefaultParent(),
                    this.originalChildEdge,
                    graph.getModel().getChildCount(graph.getDefaultParent())
                );
                graph.getModel().setTerminal(this.originalChildEdge, this.parentToAutoConnect, true);
                graph.getModel().setTerminal(this.originalChildEdge, this.childToRewire.getJCell(), false);
            } else {
                this.originalChildEdge = (mxCell) graph.insertEdge(
                    graph.getDefaultParent(), null, this.originalChildEdgeLabel,
                    this.parentToAutoConnect, this.childToRewire.getJCell()
                );
            }
        } finally {
            graph.getModel().endUpdate();
        }
    }
}
