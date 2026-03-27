package controllers.commands;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import controllers.ConstantController;
import entities.cells.Cell;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.JDBCTableCell;
import entities.cells.MemoryTableCell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.cells.XMLTableCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellRepository;
import entities.utils.cells.CellUtils;
import enums.CellType;
import enums.OperationType;
import gui.frames.main.MainFrame;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.unaryop.Reference;
import ibd.query.unaryop.filter.Condition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable snapshot of the entire canvas state (visual + business logic).
 * Used by {@link UndoRedoManager} to implement timeline-based undo/redo.
 */
final class CanvasSnapshot {

    // -----------------------------------------------------------------------
    // Snapshot data model

    /** All information needed to restore a single vertex cell. */
    private record CellSnap(
        boolean isTable,
        String name,           // display name (may be user-renamed)
        String alias,
        String opTypeName,     // OperationType.name() — null for tables
        List<String> args,     // null for tables
        boolean initialized,   // hasBeenInitialized — false for tables
        double x, double y, double w, double h
    ) {}

    /** Indices (into {@code cells} list) of source → target for each edge. */
    private record EdgeSnap(int srcIdx, int tgtIdx) {}

    // -----------------------------------------------------------------------
    // Fields

    private final List<CellSnap> cells;
    private final List<EdgeSnap> edges;

    private CanvasSnapshot(List<CellSnap> cells, List<EdgeSnap> edges) {
        this.cells = cells;
        this.edges = edges;
    }

    // -----------------------------------------------------------------------
    // Capture

    /** Captures the current canvas state into a new snapshot. */
    static CanvasSnapshot capture() {
        Map<mxCell, Integer> jCellToIdx = new LinkedHashMap<>();
        List<CellSnap> cellSnaps = new ArrayList<>();
        List<EdgeSnap> edgeSnaps = new ArrayList<>();

        // Walk every entry in ACTIVE_CELLS — skip edges (they have no Cell entry anyway)
        for (Map.Entry<com.mxgraph.model.mxICell, Cell> entry
                : CellRepository.getActiveCells().entrySet()) {

            com.mxgraph.model.mxICell rawJCell = entry.getKey();
            if (!(rawJCell instanceof mxCell jCell)) continue;
            if (jCell.isEdge()) continue;

            Cell cell = entry.getValue();
            mxGeometry geo = jCell.getGeometry();
            if (geo == null) continue;

            int idx = cellSnaps.size();
            jCellToIdx.put(jCell, idx);

            if (cell instanceof OperationCell oc) {
                cellSnaps.add(new CellSnap(
                    false,
                    oc.getName(),
                    oc.getAlias(),
                    oc.getType().name(),
                    new ArrayList<>(oc.getArguments()),
                    oc.hasBeenInitialized(),
                    geo.getX(), geo.getY(), geo.getWidth(), geo.getHeight()
                ));
            } else {
                // TableCell – name is the key in the tables map
                cellSnaps.add(new CellSnap(
                    true,
                    cell.getName(),
                    cell.getAlias(),
                    null, null, false,
                    geo.getX(), geo.getY(), geo.getWidth(), geo.getHeight()
                ));
            }
        }

        // Build edge list from parent-child relationships (avoids iterating graph edges directly)
        for (Map.Entry<com.mxgraph.model.mxICell, Cell> entry
                : CellRepository.getActiveCells().entrySet()) {
            if (!(entry.getKey() instanceof mxCell tgtJCell)) continue;
            Cell cell = entry.getValue();
            if (!(cell instanceof OperationCell oc)) continue;

            Integer tgtIdx = jCellToIdx.get(tgtJCell);
            if (tgtIdx == null) continue;

            for (Cell parent : oc.getParents()) {
                Integer srcIdx = jCellToIdx.get((mxCell) parent.getJCell());
                if (srcIdx != null) {
                    edgeSnaps.add(new EdgeSnap(srcIdx, tgtIdx));
                }
            }
        }

        return new CanvasSnapshot(cellSnaps, edgeSnaps);
    }

    // -----------------------------------------------------------------------
    // Restore

    /**
     * Clears the current canvas and rebuilds it from this snapshot.
     *
     * @param tables The application-level table registry (MainController.getTables()).
     */
    void restore(Map<String, TableCell> tables) {
        controllers.MainController.isRestoring = true;
        mxGraph graph = MainFrame.getGraph();

        // 1. Remove all visual cells from the graph
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()), true);
        } finally {
            graph.getModel().endUpdate();
        }

        // 2. Clear both repository maps (Cell objects for tables still live in 'tables' map)
        CellRepository.clearAll();

        // 3. Re-create cells
        Cell[] created = new Cell[cells.size()];

        graph.getModel().beginUpdate();
        try {
            for (int i = 0; i < cells.size(); i++) {
                CellSnap snap = cells.get(i);

                if (snap.isTable()) {
                    TableCell template = tables.get(snap.name());
                    if (template == null) continue;

                    // Always create a fresh mxCell so that multiple instances of the
                    // same table (same name) each get their own distinct vertex.
                    CellType cellType = CellType.fromTableCell(template);
                    mxCell jCell = (mxCell) graph.insertVertex(
                        graph.getDefaultParent(), null, snap.name(),
                        snap.x(), snap.y(), snap.w(), snap.h(), cellType.id);

                    // Copy constructor auto-registers the cell in CellRepository
                    // (via Cell base constructor -> CellUtils.addCell).
                    TableCell tc = switch (cellType) {
                        case CSV_TABLE    -> new CSVTableCell((CSVTableCell) template, jCell);
                        case FYI_TABLE    -> new FYITableCell((FYITableCell) template, jCell);
                        case MEMORY_TABLE -> new MemoryTableCell((MemoryTableCell) template, jCell);
                        case XML_TABLE    -> new XMLTableCell((XMLTableCell) template, jCell);
                        case JDBC_TABLE   -> new JDBCTableCell((JDBCTableCell) template, jCell);
                        default -> null;
                    };
                    if (tc == null) continue;

                    tc.setAlias(snap.alias());
                    tc.removeChild();   // clear stale child ref from prior canvas state
                    created[i] = tc;

                } else {
                    // OperationCell — create a brand-new instance
                    OperationType opType;
                    try {
                        opType = OperationType.valueOf(snap.opTypeName());
                    } catch (IllegalArgumentException e) {
                        continue; // unknown type
                    }

                    mxCell jCell = (mxCell) graph.insertVertex(
                        graph.getDefaultParent(), null, snap.name(),
                        snap.x(), snap.y(), snap.w(), snap.h(), CellType.OPERATION.id
                    );

                    // OperationCell constructor registers itself in CellRepository
                    OperationCell oc = new OperationCell(jCell, opType);
                    oc.setAlias(snap.alias());

                    // Restore the user-visible name if it was renamed
                    if (!snap.name().equals(opType.getFormattedDisplayName())) {
                        CellUtils.changeCellName(jCell, snap.name());
                    }

                    // Set default operators for special types (mirrors InsertOperationCellCommand)
                    if (opType == OperationType.CONDITION) {
                        try { oc.setOperator(new Condition(new NoLookupFilter())); } catch (Exception ignored) {}
                    } else if (opType == OperationType.REFERENCE) {
                        try { oc.setOperator(new Reference(new ArrayList<String>().toArray(new String[0]))); } catch (Exception ignored) {}
                    }

                    created[i] = oc;
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // 4. Reconnect parent-child relationships and add visual edges
        graph.getModel().beginUpdate();
        try {
            for (EdgeSnap e : edges) {
                Cell src = (e.srcIdx() < created.length) ? created[e.srcIdx()] : null;
                Cell tgt = (e.tgtIdx() < created.length) ? created[e.tgtIdx()] : null;
                if (src == null || !(tgt instanceof OperationCell oc)) continue;

                graph.insertEdge(graph.getDefaultParent(), null, "",
                    src.getJCell(), tgt.getJCell());
                oc.addParent(src);
                src.setChild(oc);
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // 5. Restore configured state for initialised OperationCells (after all parents connected)
        for (int i = 0; i < cells.size(); i++) {
            CellSnap snap = cells.get(i);
            if (snap.isTable() || !snap.initialized()) continue;
            if (!(created[i] instanceof OperationCell oc)) continue;

            oc.setArguments(new ArrayList<>(snap.args()));
            oc.setAlias(snap.alias());
        }

        // 6. Recalculate trees bottom-up from every table root
        for (Cell c : created) {
            if (c instanceof TableCell) {
                TreeUtils.updateTreeBelow(c);
            }
        }

        graph.refresh();
        controllers.MainController.isRestoring = false;
    }
}
