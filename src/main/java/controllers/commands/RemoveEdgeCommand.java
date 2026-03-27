package controllers.commands;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellRepository;
import gui.frames.main.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoveEdgeCommand extends BaseUndoableRedoableCommand {

    private final mxCell edgeJCell;

    // saved state for undo
    private mxCell sourceJCell;
    private mxCell targetJCell;
    private Cell sourceCell;
    private OperationCell targetCell;

    public RemoveEdgeCommand(mxCell edgeJCell) {
        this.edgeJCell = edgeJCell;
    }

    @Override
    public void execute() {
        if (!edgeJCell.isEdge()) return;

        // Capture source and target before removal
        this.sourceJCell = (mxCell) edgeJCell.getSource();
        this.targetJCell = (mxCell) edgeJCell.getTarget();

        Optional<Cell> optSource = CellRepository.getActiveCell(this.sourceJCell);
        Optional<Cell> optTarget = CellRepository.getActiveCell(this.targetJCell);

        if (optSource.isEmpty() || optTarget.isEmpty()) return;

        this.sourceCell = optSource.get();
        this.targetCell = (OperationCell) optTarget.get();

        // Disconnect in the business logic layer
        this.targetCell.removeParent(this.sourceCell);
        this.sourceCell.setChild(null);

        List<Cell> parents = new ArrayList<>();
        parents.add(this.sourceCell);
        TreeUtils.updateTreesAboveAndBelow(parents, this.targetCell);

        // Remove from graph
        MainFrame.getGraph().getModel().beginUpdate();
        try {
            MainFrame.getGraph().getModel().remove(edgeJCell);
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
        }
        MainFrame.getGraph().refresh();
    }

    @Override
    public void undo() {
        if (this.sourceCell == null || this.targetCell == null) return;

        // Reconnect in the business logic layer
        this.targetCell.addParent(this.sourceCell);
        this.sourceCell.setChild(this.targetCell);

        List<Cell> parents = new ArrayList<>();
        parents.add(this.sourceCell);
        TreeUtils.updateTreesAboveAndBelow(parents, this.targetCell);

        // Re-add the edge to the graph
        MainFrame.getGraph().getModel().beginUpdate();
        try {
            MainFrame.getGraph().getModel().add(
                MainFrame.getGraph().getDefaultParent(),
                edgeJCell,
                MainFrame.getGraph().getModel().getChildCount(MainFrame.getGraph().getDefaultParent())
            );
            edgeJCell.setSource(this.sourceJCell);
            edgeJCell.setTarget(this.targetJCell);
            MainFrame.getGraph().getModel().setTerminal(edgeJCell, this.sourceJCell, true);
            MainFrame.getGraph().getModel().setTerminal(edgeJCell, this.targetJCell, false);
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
        }
        MainFrame.getGraph().refresh();
    }

    @Override
    public void redo() {
        execute();
    }
}
