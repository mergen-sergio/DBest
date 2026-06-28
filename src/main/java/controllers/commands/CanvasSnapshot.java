package controllers.commands;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import utils.TopologicalSort;
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

public final class CanvasSnapshot {

    private record CellSnap(
        boolean isTable,
        String name,
        String alias,
        String opTypeName,
        List<String> args,
        boolean initialized,
        double x, double y, double w, double h
    ) {}

    private record EdgeSnap(int srcIdx, int tgtIdx, String label) {}

    private final List<CellSnap> cells;
    private final List<EdgeSnap> edges;

    private CanvasSnapshot(List<CellSnap> cells, List<EdgeSnap> edges) {
        this.cells = cells;
        this.edges = edges;
    }

    public static CanvasSnapshot capture() {
        Map<mxCell, Integer> jCellToIdx = new LinkedHashMap<>();
        List<CellSnap> cellSnaps = new ArrayList<>();
        List<EdgeSnap> edgeSnaps = new ArrayList<>();

        mxGraph graph = MainFrame.getGraph();

        for (Map.Entry<com.mxgraph.model.mxICell, Cell> entry
                : CellRepository.getActiveCells().entrySet()) {

            com.mxgraph.model.mxICell rawJCell = entry.getKey();
            if (!(rawJCell instanceof mxCell jCell)) continue;
            if (jCell.isEdge()) continue;
            if (!graph.getModel().contains(jCell)) continue;

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
                cellSnaps.add(new CellSnap(
                    true,
                    cell.getName(),
                    cell.getAlias(),
                    null, null, false,
                    geo.getX(), geo.getY(), geo.getWidth(), geo.getHeight()
                ));
            }
        }

        for (Map.Entry<com.mxgraph.model.mxICell, Cell> entry
                : CellRepository.getActiveCells().entrySet()) {
            if (!(entry.getKey() instanceof mxCell tgtJCell)) continue;
            if (!graph.getModel().contains(tgtJCell)) continue;
            Cell cell = entry.getValue();
            if (!(cell instanceof OperationCell oc)) continue;

            Integer tgtIdx = jCellToIdx.get(tgtJCell);
            if (tgtIdx == null) continue;

            for (Cell parent : oc.getParents()) {
                mxCell parentJCell = (mxCell) parent.getJCell();
                Integer srcIdx = jCellToIdx.get(parentJCell);
                if (srcIdx == null) continue;

                String edgeLabel = "";
                for (Object inEdge : graph.getIncomingEdges(tgtJCell)) {
                    if (inEdge instanceof mxCell edgeMxCell
                            && edgeMxCell.getSource() == parentJCell) {
                        Object val = edgeMxCell.getValue();
                        if (val instanceof String s) edgeLabel = s;
                        break;
                    }
                }
                edgeSnaps.add(new EdgeSnap(srcIdx, tgtIdx, edgeLabel));
            }
        }

        return new CanvasSnapshot(cellSnaps, edgeSnaps);
    }

    void restore(Map<String, TableCell> tables) {
        controllers.MainController.isRestoring = true;
        mxGraph graph = MainFrame.getGraph();
        try {
            graph.getModel().beginUpdate();
            try {
                graph.removeCells(graph.getChildVertices(graph.getDefaultParent()), true);
            } finally {
                graph.getModel().endUpdate();
            }

            CellRepository.clearAll();

            Cell[] created = new Cell[cells.size()];

            graph.getModel().beginUpdate();
            try {
                for (int i = 0; i < cells.size(); i++) {
                    CellSnap snap = cells.get(i);

                    if (snap.isTable()) {
                        TableCell template = tables.get(snap.name());
                        if (template == null) continue;

                        CellType cellType = CellType.fromTableCell(template);
                        mxCell jCell = (mxCell) graph.insertVertex(
                            graph.getDefaultParent(), null, snap.name(),
                            snap.x(), snap.y(), snap.w(), snap.h(), cellType.id);

                        TableCell tc = switch (cellType) {
                            case CSV_TABLE    -> new CSVTableCell((CSVTableCell) template, jCell);
                            case FYI_TABLE    -> new FYITableCell((FYITableCell) template, jCell);
                            case MEMORY_TABLE -> new MemoryTableCell((MemoryTableCell) template, jCell);
                            case XML_TABLE    -> new XMLTableCell((XMLTableCell) template, jCell);
                            case JDBC_TABLE   -> new JDBCTableCell((JDBCTableCell) template, jCell);
                            default -> null;
                        };
                        if (tc == null) continue;

                        if (snap.alias() != null && !snap.alias().isBlank()) {
                            tc.asOperator(snap.alias());
                        }
                        tc.removeChild();
                        created[i] = tc;

                    } else {
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

                        OperationCell oc = new OperationCell(jCell, opType);
                        oc.setAlias(snap.alias());

                        if (!snap.name().equals(opType.getFormattedDisplayName())) {
                            CellUtils.changeCellName(jCell, snap.name());
                        }

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

            graph.getModel().beginUpdate();
            try {
                for (EdgeSnap e : edges) {
                    Cell src = (e.srcIdx() < created.length) ? created[e.srcIdx()] : null;
                    Cell tgt = (e.tgtIdx() < created.length) ? created[e.tgtIdx()] : null;
                    if (src == null || !(tgt instanceof OperationCell oc)) continue;

                    graph.insertEdge(graph.getDefaultParent(), null, e.label(),
                        src.getJCell(), tgt.getJCell());
                    oc.addParent(src);
                    src.setChild(oc);
                }
            } finally {
                graph.getModel().endUpdate();
            }

            {
                List<OperationCell> opCells = new ArrayList<>();
                for (int i = 0; i < cells.size(); i++) {
                    CellSnap snap = cells.get(i);
                    if (snap.isTable() || !snap.initialized()) continue;
                    if (!(created[i] instanceof OperationCell oc)) continue;
                    oc.setArguments(new ArrayList<>(snap.args()));
                    oc.setAlias(snap.alias());
                    opCells.add(oc);
                }

                List<OperationCell> sorted = TopologicalSort.sort(opCells, oc ->
                    oc.getParents().stream()
                        .filter(p -> p instanceof OperationCell)
                        .map(p -> (OperationCell) p)
                        .collect(java.util.stream.Collectors.toList())
                );
                for (OperationCell oc : sorted) {
                    oc.updateOperation();
                }
            }

            for (Cell c : created) {
                if (c instanceof TableCell) {
                    TreeUtils.updateTreeBelow(c);
                }
            }

            graph.refresh();
        } finally {
            controllers.MainController.isRestoring = false;
        }
    }
}
