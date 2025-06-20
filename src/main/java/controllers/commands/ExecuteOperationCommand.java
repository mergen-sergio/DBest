package controllers.commands;

import com.mxgraph.model.mxCell;
import controllers.MainController;
import entities.Edge;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import entities.utils.edges.EdgeUtils;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IOperationForm;
import operations.IOperator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ExecuteOperationCommand extends BaseUndoableRedoableCommand {

    private final AtomicReference<mxCell> cellReference;

    private final AtomicReference<mxCell> invisibleCellReference;

    private final AtomicReference<Edge> edgeReference;

    private final Cell cell;
    private Cell cellCopy;

    public ExecuteOperationCommand(
        @NotNull AtomicReference<mxCell> cellReference,
        @NotNull AtomicReference<mxCell> invisibleCellReference,
        @NotNull AtomicReference<Edge> edgeReference,
        @NotNull Cell cell
    ) {
        this.cellReference = cellReference;
        this.invisibleCellReference = invisibleCellReference;
        this.edgeReference = edgeReference;
        this.cell = cell;
        this.cellCopy = null;
    }

    @Override
    public void execute() {
        if (!(this.cell instanceof OperationCell operationCell)) return;

        CellUtils.removeCell(this.invisibleCellReference);

        EdgeUtils.addEdge(this.edgeReference.get(), this.cellReference.get());

        Optional<Cell> optionalParentCell = CellUtils.getActiveCell(this.edgeReference.get().getParent());

        if (optionalParentCell.isEmpty()) {
            operationCell.addParent(null);
            return;
        }

        Cell parentCell = optionalParentCell.get();

        operationCell.addParent(parentCell);

        parentCell.setChild(operationCell);

        operationCell.setAllNewTrees();

        TreeUtils.recalculateContent(operationCell);

        OperationType operationType = operationCell.getType();

        if (
            (operationCell.getArity() == OperationArity.UNARY || operationCell.getParents().size() == 2) &&
            !OperationType.OPERATIONS_WITHOUT_FORM.contains(operationType)
        ) {
            this.executeOperationWithForm(operationType);
        }

        if (
            (operationCell.getArity() == OperationArity.UNARY || operationCell.getParents().size() == 2) &&
            OperationType.OPERATIONS_WITHOUT_FORM.contains(operationType)
        ) {
            this.executeOperationWithoutForm(operationType, operationCell.getAlias());
        }
        
        //TO DO: added to recalculate after operation created.
        //need to check if the recalculate before creation (a few lines of code above) is really needed
        TreeUtils.recalculateContent(operationCell);
    }

    @Override
    public void undo() {
        if (!(this.cell instanceof OperationCell operationCell)) return;

        this.cellCopy = operationCell.copy();

        operationCell.reset();

        Optional<Cell> optionalParentCell = CellUtils.getActiveCell(this.edgeReference.get().getParent());

        if (optionalParentCell.isEmpty()) return;

        Cell parentCell = optionalParentCell.get();

        operationCell.removeParent(parentCell);

        parentCell.setChild(null);

        operationCell.setAllNewTrees();

        TreeUtils.recalculateContent(operationCell);
    }

    @Override
    public void redo() {
        if (!(
            this.cell instanceof OperationCell operationCell &&
            this.cellCopy instanceof OperationCell operationCellCopy
        )) return;

        operationCell.updateFrom(operationCellCopy);

        Optional<Cell> optionalParentCell = CellUtils.getActiveCell(this.edgeReference.get().getParent());

        if (optionalParentCell.isEmpty()) return;

        Cell parentCell = optionalParentCell.get();

        parentCell.setChild(operationCell);

        operationCell.setAllNewTrees();

        TreeUtils.recalculateContent(operationCell);
    }

    private void executeOperationWithForm(OperationType operationType) {
        try {
            MainController.setPopupBeingActivatedByCommand(true);
            
            Constructor<? extends IOperationForm> constructor = operationType.form.getDeclaredConstructor(mxCell.class);
            constructor.newInstance(this.cellReference.get());
        } catch (
            InstantiationException | IllegalAccessException |
            NoSuchMethodException | InvocationTargetException exception
        ) {
            exception.printStackTrace();
        } finally {
            MainController.setPopupBeingActivatedByCommand(false);
        }
    }

    private void executeOperationWithoutForm(OperationType operationType, String alias) {
        try {
            Constructor<? extends IOperator> constructor = operationType.operatorClass.getDeclaredConstructor();
            constructor.newInstance().executeOperation(this.cellReference.get(), List.of(), alias);
        } catch (
            InstantiationException | IllegalAccessException |
            NoSuchMethodException | InvocationTargetException exception
        ) {
            exception.printStackTrace();
        }
    }
}
