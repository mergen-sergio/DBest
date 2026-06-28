package controllers.commands;

import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.utils.cells.CellUtils;
import gui.frames.main.MainFrame;

/**
 * Records deletion of a node (vertex).  Before removal the connected edges are
 * saved so they can be visually restored on undo.
 */
public class RemoveCellCommand extends BaseUndoableRedoableCommand {

    private final AtomicReference<mxCell> cellReference;

    // Edges connected to this cell saved before the cascade-removal
    private mxCell[] savedEdges = new mxCell[0];
    private mxCell[] savedEdgeSources = new mxCell[0];
    private mxCell[] savedEdgeTargets = new mxCell[0];

    public RemoveCellCommand(AtomicReference<mxCell> cellReference) {
        this.cellReference = cellReference;
    }

    @Override
    public void execute() {
        captureEdges(cellReference.get());
        CellUtils.deactivateActiveJCell(MainFrame.getGraph(), this.cellReference.get());
    }

    @Override
    public void undo() {
        // Restores business-logic parent/child refs via the cell's own preserved state
        CellUtils.activateInactiveJCell(MainFrame.getGraph(), this.cellReference.get());
        restoreEdges();
    }

    @Override
    public void redo() {
        captureEdges(cellReference.get());
        CellUtils.deactivateActiveJCell(MainFrame.getGraph(), this.cellReference.get());
    }

    // -------------------------------------------------------------------------

    private void captureEdges(mxCell jCell) {
        Object[] edges = MainFrame.getGraph().getEdges(jCell);
        savedEdges = new mxCell[edges.length];
        savedEdgeSources = new mxCell[edges.length];
        savedEdgeTargets = new mxCell[edges.length];
        for (int i = 0; i < edges.length; i++) {
            savedEdges[i] = (mxCell) edges[i];
            savedEdgeSources[i] = (mxCell) savedEdges[i].getSource();
            savedEdgeTargets[i] = (mxCell) savedEdges[i].getTarget();
        }
    }

    private void restoreEdges() {
        if (savedEdges.length == 0) return;
        MainFrame.getGraph().getModel().beginUpdate();
        try {
            for (int i = 0; i < savedEdges.length; i++) {
                MainFrame.getGraph().getModel().add(
                    MainFrame.getGraph().getDefaultParent(), savedEdges[i],
                    MainFrame.getGraph().getModel()
                        .getChildCount(MainFrame.getGraph().getDefaultParent())
                );
                MainFrame.getGraph().getModel().setTerminal(savedEdges[i], savedEdgeSources[i], true);
                MainFrame.getGraph().getModel().setTerminal(savedEdges[i], savedEdgeTargets[i], false);
            }
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
        }
        MainFrame.getGraph().refresh();
    }
}
