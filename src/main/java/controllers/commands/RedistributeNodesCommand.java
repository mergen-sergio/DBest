package controllers.commands;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import entities.Tree;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.main.MainFrame;

import java.util.*;
import java.util.List;

/**
 * Comando para redistribuir nós de uma árvore em layout hierárquico
 * a partir de um nó selecionado como referência.
 */
public class RedistributeNodesCommand implements UndoableRedoableCommand {

    private final mxCell selectedCell;
    private final Map<mxCell, mxGeometry> originalPositions;
    private final Map<mxCell, mxGeometry> newPositions;

    public RedistributeNodesCommand(mxCell selectedCell) {
        this.selectedCell = selectedCell;
        this.originalPositions = new HashMap<>();
        this.newPositions = new HashMap<>();
    }

    @Override
    public void execute() {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(selectedCell);
        if (optionalCell.isEmpty()) {
            return;
        }

        Cell rootCell = optionalCell.get();
        Tree tree = rootCell.getTree();

        if (tree == null) {
            return;
        }

        // Salvar posições originais
        saveOriginalPositions(tree);

        // Calcular novas posições usando layout hierárquico
        calculateNewPositions(rootCell);

        // Aplicar novas posições
        applyNewPositions();
    }

    @Override
    public void undo() {
        // Restaurar posições originais
        mxGraph graph = MainFrame.getGraph();
        graph.getModel().beginUpdate();
        try {
            for (Map.Entry<mxCell, mxGeometry> entry : originalPositions.entrySet()) {
                mxCell cell = entry.getKey();
                mxGeometry originalGeometry = entry.getValue();
                mxGeometry currentGeometry = cell.getGeometry();

                if (currentGeometry != null && originalGeometry != null) {
                    currentGeometry.setX(originalGeometry.getX());
                    currentGeometry.setY(originalGeometry.getY());
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
        graph.refresh();
    }

    @Override
    public void redo() {
        // Reaplicar novas posições
        applyNewPositions();
    }

    @Override
    public String getName() {
        return "Reorganizar Árvore";
    }

    private void saveOriginalPositions(Tree tree) {
        Set<Cell> cells = tree.getCells();
        for (Cell cell : cells) {
            mxCell jCell = cell.getJCell();
            if (jCell != null && jCell.getGeometry() != null) {
                mxGeometry originalGeometry = (mxGeometry) jCell.getGeometry().clone();
                originalPositions.put(jCell, originalGeometry);
            }
        }
    }

    private void calculateNewPositions(Cell rootCell) {
        // Configurações do layout
        final double HORIZONTAL_SPACING = 150.0;
        final double VERTICAL_SPACING = 80.0;

        // Obter posição de referência do nó selecionado
        mxGeometry rootGeometry = rootCell.getJCell().getGeometry();
        double rootX = rootGeometry.getX();
        double rootY = rootGeometry.getY();

        // Organizar nós por níveis hierárquicos
        Map<Integer, List<Cell>> levelMap = organizeCellsByLevels(rootCell);

        // Calcular posições para cada nível
        for (Map.Entry<Integer, List<Cell>> entry : levelMap.entrySet()) {
            int level = entry.getKey();
            List<Cell> cellsInLevel = entry.getValue();

            // Calcular Y baseado no nível
            double levelY = rootY + (level * VERTICAL_SPACING);

            // Calcular posições X para centralizar os nós no nível
            double totalWidth = (cellsInLevel.size() - 1) * HORIZONTAL_SPACING;
            double startX = rootX - (totalWidth / 2.0);

            for (int i = 0; i < cellsInLevel.size(); i++) {
                Cell cell = cellsInLevel.get(i);
                mxCell jCell = cell.getJCell();

                if (jCell != null && jCell.getGeometry() != null) {
                    double newX = startX + (i * HORIZONTAL_SPACING);
                    double newY = levelY;

                    mxGeometry newGeometry = (mxGeometry) jCell.getGeometry().clone();
                    newGeometry.setX(newX);
                    newGeometry.setY(newY);
                    newPositions.put(jCell, newGeometry);
                }
            }
        }
    }

    private Map<Integer, List<Cell>> organizeCellsByLevels(Cell rootCell) {
        Map<Integer, List<Cell>> levelMap = new HashMap<>();
        Queue<CellLevel> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();

        // Começar com o nó raiz no nível 0
        queue.offer(new CellLevel(rootCell, 0));
        visited.add(rootCell);

        while (!queue.isEmpty()) {
            CellLevel current = queue.poll();
            Cell cell = current.cell;
            int level = current.level;

            // Adicionar célula ao nível correspondente
            levelMap.computeIfAbsent(level, k -> new ArrayList<>()).add(cell);

            // Adicionar filhos (pais na estrutura de árvore de consulta)
            if (cell.hasParents()) {
                for (Cell parent : cell.getParents()) {
                    if (!visited.contains(parent)) {
                        queue.offer(new CellLevel(parent, level + 1));
                        visited.add(parent);
                    }
                }
            }
        }

        return levelMap;
    }

    private void applyNewPositions() {
        mxGraph graph = MainFrame.getGraph();
        graph.getModel().beginUpdate();
        try {
            for (Map.Entry<mxCell, mxGeometry> entry : newPositions.entrySet()) {
                mxCell cell = entry.getKey();
                mxGeometry newGeometry = entry.getValue();
                mxGeometry currentGeometry = cell.getGeometry();

                if (currentGeometry != null) {
                    currentGeometry.setX(newGeometry.getX());
                    currentGeometry.setY(newGeometry.getY());
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
        graph.refresh();
    }

    /**
     * Classe auxiliar para armazenar célula e seu nível
     */
    private static class CellLevel {
        final Cell cell;
        final int level;

        CellLevel(Cell cell, int level) {
            this.cell = cell;
            this.level = level;
        }
    }
}
