package controllers.commands;

import com.mxgraph.model.mxCell;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellRepository;
import entities.utils.cells.CellUtils;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.main.MainFrame;
import operations.IOperator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Flat command that records the creation of an edge between a parent cell and
 * an operation cell.  No nested CommandControllers — all logic lives here.
 *
 * execute : removes the invisible tracking cell, inserts a real edge, sets up
 *           the parent-child business-logic link, opens the configuration form
 *           when the operation has all its required parents.
 * undo    : disconnects the parent-child link and removes the visual edge.
 * redo    : re-inserts the visual edge and reconnects the parent-child link.
 */
public class ConnectNodesCommand extends BaseUndoableRedoableCommand {

    // --- construction-time ---
    private final mxCell sourceJCell;
    private final mxCell targetJCell;
    private final AtomicReference<mxCell> invisibleCellReference;

    // --- resolved in execute() ---
    private mxCell edgeJCell;
    private Cell sourceCell;
    private OperationCell operationCell;

    /** true when this command itself opened the configuration form */
    private boolean initializedOnExecute;

    // --- saved in undo() for use by redo() ---
    private List<String> savedArgs;
    private String savedAlias;
    private boolean savedHasInit;
    private String savedName;

    public ConnectNodesCommand(
        mxCell sourceJCell,
        mxCell targetJCell,
        AtomicReference<mxCell> invisibleCellReference
    ) {
        this.sourceJCell = sourceJCell;
        this.targetJCell = targetJCell;
        this.invisibleCellReference = invisibleCellReference;
    }

    // -------------------------------------------------------------------------
    @Override
    public void execute() {
        // 1. Remove the invisible tracking cell (permanent — not undone)
        mxCell invisible = (invisibleCellReference != null) ? invisibleCellReference.get() : null;
        if (invisible != null) {
            MainFrame.getGraph().removeCells(new Object[]{invisible}, true);
            invisibleCellReference.set(null);
        }

        // 2. Resolve business-logic cells
        Optional<Cell> optSource = CellRepository.getActiveCell(sourceJCell);
        Optional<Cell> optTarget = CellRepository.getActiveCell(targetJCell);
        if (optSource.isEmpty() || optTarget.isEmpty()) return;
        if (!(optTarget.get() instanceof OperationCell opCell)) return;

        this.sourceCell = optSource.get();
        this.operationCell = opCell;

        // 3. Insert the visual edge
        this.edgeJCell = (mxCell) MainFrame.getGraph().insertEdge(
            MainFrame.getGraph().getDefaultParent(), null, "",
            sourceJCell, targetJCell
        );

        // 4. Set up parent-child business logic
        boolean hadInit = operationCell.hasBeenInitialized();
        operationCell.addParent(sourceCell);
        sourceCell.setChild(operationCell);
        operationCell.setAllNewTrees();
        TreeUtils.recalculateContent(operationCell);

        // 5. Open configuration form / execute form-less operation if ready
        OperationType opType = operationCell.getType();
        boolean isReady = operationCell.getArity() == OperationArity.UNARY
                       || operationCell.getParents().size() == 2;

        if (isReady && !OperationType.OPERATIONS_WITHOUT_FORM.contains(opType)) {
            executeWithForm(opType);
        } else if (isReady && OperationType.OPERATIONS_WITHOUT_FORM.contains(opType)) {
            executeWithoutForm(opType, operationCell.getAlias());
        }

        // 6. Final recalculation (applies any changes made by the form)
        TreeUtils.recalculateContent(operationCell);

        this.initializedOnExecute = !hadInit && operationCell.hasBeenInitialized();
    }

    // -------------------------------------------------------------------------
    @Override
    public void undo() {
        if (sourceCell == null || operationCell == null) return;

        // Save current operation state so redo can restore it
        this.savedArgs = new ArrayList<>(operationCell.getArguments());
        this.savedAlias = operationCell.getAlias();
        this.savedHasInit = operationCell.hasBeenInitialized();
        this.savedName = operationCell.getName();

        // If this command was the one that initialised the operation
        // (opened the form / ran the form-less setup), we must clean up
        // all parent mutual-refs before calling reset(), because reset()
        // clears the parents list and sets the node label back to the
        // default type name.
        if (this.initializedOnExecute) {
            for (Cell otherParent : new ArrayList<>(operationCell.getParents())) {
                if (!otherParent.equals(sourceCell)) {
                    otherParent.setChild(null);
                }
            }
            operationCell.removeParent(sourceCell);
            sourceCell.setChild(null);
            operationCell.reset(); // clears remaining parents + args + name
        } else {
            operationCell.removeParent(sourceCell);
            sourceCell.setChild(null);
        }

        operationCell.setAllNewTrees();
        TreeUtils.recalculateContent(operationCell);

        // Remove the visual edge (preserves edgeJCell.source / .target in mxCell fields)
        MainFrame.getGraph().getModel().beginUpdate();
        try {
            MainFrame.getGraph().getModel().remove(edgeJCell);
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
        }
        MainFrame.getGraph().refresh();
    }

    // -------------------------------------------------------------------------
    @Override
    public void redo() {
        if (sourceCell == null || operationCell == null || edgeJCell == null) return;

        // Re-add the visual edge with its original endpoints
        MainFrame.getGraph().getModel().beginUpdate();
        try {
            MainFrame.getGraph().getModel().add(
                MainFrame.getGraph().getDefaultParent(), edgeJCell,
                MainFrame.getGraph().getModel()
                    .getChildCount(MainFrame.getGraph().getDefaultParent())
            );
            MainFrame.getGraph().getModel().setTerminal(edgeJCell, sourceJCell, true);
            MainFrame.getGraph().getModel().setTerminal(edgeJCell, targetJCell, false);
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
        }

        // Restore parent-child link
        operationCell.addParent(sourceCell);
        sourceCell.setChild(operationCell);

        // Restore the operation configuration that was saved during undo()
        if (this.savedHasInit) {
            operationCell.setArguments(new ArrayList<>(this.savedArgs));
            operationCell.setAlias(this.savedAlias);
            operationCell.setName(this.savedName);
            MainFrame.getGraph().getModel().setValue(targetJCell, this.savedName);
        }

        operationCell.setAllNewTrees();
        TreeUtils.recalculateContent(operationCell);
        MainFrame.getGraph().refresh();
    }

    // -------------------------------------------------------------------------
    private void executeWithForm(OperationType operationType) {
        try {
            MainController.setPopupBeingActivatedByCommand(true);
            Constructor<? extends IOperationForm> ctor =
                operationType.form.getDeclaredConstructor(mxCell.class);
            ctor.newInstance(this.targetJCell);
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            MainController.setPopupBeingActivatedByCommand(false);
        }
    }

    private void executeWithoutForm(OperationType operationType, String alias) {
        try {
            Constructor<? extends IOperator> ctor =
                operationType.operatorClass.getDeclaredConstructor();
            ctor.newInstance().executeOperation(this.targetJCell, List.of(), alias);
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
