package controllers;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import controllers.commands.*;
import database.TableCreator;
import dsl.entities.BinaryExpression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import engine.exceptions.DataBaseException;
import entities.Action.CreateOperationCellAction;
import entities.Action.CreateTableCellAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Column;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.cells.*;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import enums.CellType;
import enums.FileType;
import enums.OperationType;
import files.ExportFile;
import files.FileUtils;
import files.ImportFile;
import files.csv.CSVInfo;
import gui.frames.CellInformationFrame;
import gui.frames.ComparatorFrame;
import gui.frames.ErrorFrame;
import gui.frames.dsl.ConsoleFrame;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.CSVRecognizerForm;
import gui.frames.forms.importexport.ExportAsForm;
import gui.frames.forms.importexport.ImportAsForm;
import gui.frames.forms.importexport.PKAndNameChooserForm;
import gui.frames.forms.operations.unary.AsOperatorForm;
import gui.frames.jdbc.ConnectionsFrame;
import gui.frames.main.MainFrame;
import ibd.query.SingleSource;
import utils.RandomUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class MainController extends MainFrame {

    private final Container textEditor = new TextEditor(this).getContentPane();

    private mxCell jCell, ghostCell = null;

    private final AtomicReference<mxCell> invisibleCellReference = new AtomicReference<>(null);

    private final AtomicReference<CurrentAction> currentActionReference = new AtomicReference<>(ConstantController.NONE_ACTION);

    public static final AtomicReference<Edge> currentEdgeReference = new AtomicReference<>(new Edge());

    private static final Map<Integer, Tree> trees = new HashMap<>();

    private static final Map<String, TableCell> tables = new HashMap<>();

    public static final CommandController commandController = new CommandController();

    private static File lastDirectory = new File("");

    public static ConsoleFrame consoleFrame = null;

    public static ComparatorFrame comparatorFrame = null;

    public static ConnectionsFrame connectionsFrame = null;

    private static int currentTableYPosition = 0;

    private boolean isTableCellSelected = false;

    public static Rectangle selectionRectangle = null; // Store the last selected rectangle
    private static Point startPoint = null; // Starting point of the rectangle

    // Map para armazenar os estilos originais das células
    //private static Map<mxCell, String> originalStyles = new HashMap<>();
    public MainController() {
        super(new HashSet<>());
        this.tablesComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                Object cell = MainController.this.tablesComponent.getCellAt(event.getX(), event.getY());

                if (cell != null) {
                    graph.setSelectionCell(new mxCell(((mxCell) cell).getValue()));
                    MainController.this.isTableCellSelected = true;
                }
            }
        });

        graph.addListener(mxEvent.CELLS_ADDED, (sender, event) -> {
            if (this.isTableCellSelected) {
                this.executeInsertTableCellCommand((mxCell) graph.getSelectionCell(), this.ghostCell);
                this.isTableCellSelected = false;
            }
        });

        // Set the background color of the graph component
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);

        new mxRubberband(graphComponent);

        graphComponent.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                graphComponent.zoomIn();
            } else {
                graphComponent.zoomOut();
            }
        });

//        graph.setAutoSizeCells(true);
        mxSwingConstants.VERTEX_SELECTION_COLOR = Color.BLUE;

        // Set the selection stroke to a solid line
        mxSwingConstants.VERTEX_SELECTION_STROKE = new BasicStroke(2.0f);

        // Add mouse listener for rectangle selection
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Get starting point in screen coordinates
                startPoint = e.getLocationOnScreen(); // Store starting point
                selectionRectangle = null; // Reset rectangle
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint != null) {
                    // Get end point in screen coordinates
                    Point endPoint = e.getLocationOnScreen(); // Get end point
                    selectionRectangle = new Rectangle(
                            Math.min(startPoint.x, endPoint.x) - getLocationOnScreen().x,
                            Math.min(startPoint.y, endPoint.y) - getLocationOnScreen().y,
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    );
                    graphComponent.getGraphControl().repaint();  // Refresh to show the rectangle

                }
            }
        });

        graphComponent.getGraphControl().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    // Get current point in screen coordinates
                    Point endPoint = e.getLocationOnScreen();
                    selectionRectangle = new Rectangle(
                            Math.min(startPoint.x, endPoint.x) - getLocationOnScreen().x,
                            Math.min(startPoint.y, endPoint.y) - getLocationOnScreen().y,
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    );
                    graphComponent.getGraphControl().repaint();  // Refresh while dragging
                }
            }
        });

        // Add mouse motion listener for real-time rectangle drawing
        graphComponent.getGraphControl().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    Point endPoint = e.getPoint();
                    selectionRectangle = new Rectangle(
                            Math.min(startPoint.x, endPoint.x),
                            Math.min(startPoint.y, endPoint.y),
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    );
                    graphComponent.getGraphControl().repaint(); // Refresh while dragging
                }
            }
        });

//        // Definir um listener para mudanças na seleção
//        graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
//            @Override
//            public void invoke(Object sender, mxEventObject evt) {
//                // Restaurar o estilo original das células que perderam a seleção
//                for (Map.Entry<mxCell, String> entry : originalStyles.entrySet()) {
//                    mxCell cell = entry.getKey();
//                    String originalStyle = entry.getValue();
//                    graph.getModel().setStyle(cell, originalStyle);
//                }
//
//                // Limpar o map para as células deselecionadas
//                originalStyles.clear();
//
//                Object[] selectedCells = graph.getSelectionCells();
//
//                // Para cada célula selecionada, altere o estilo e salve o original
//                for (Object cell : selectedCells) {
//                    if (cell instanceof mxCell) {
//                        mxCell mxCell = (mxCell) cell;
//                        String originalStyle = mxCell.getStyle();
//
//                        // Salvar o estilo original
//                        originalStyles.put(mxCell, originalStyle);
//
//                        // Aplicar novo estilo
//                        String newStyle = originalStyle + ";" 
//                                + mxConstants.STYLE_STROKECOLOR + "=blue;"
//                                + mxConstants.STYLE_STROKEWIDTH + "=3;";
//                                //+ mxConstants.STYLE_FILLCOLOR + "=yellow";
//                        graph.getModel().setStyle(mxCell, newStyle);
//                    }
//                }
//            }
//        });
        //graphComponent.getGraphControl().setTransferHandler(new FileTransferHandler());
        //graphComponent.setImportEnabled(true);
        //CustomDropTarget customDropTarget = new CustomDropTarget(graphComponent);
//        customDropTarget.addDropTargetListener(new DropTargetListener() {
//        // Add drag-and-drop support
//            public void dragEnter(DropTargetDragEvent dtde) {}
//            public void dragOver(DropTargetDragEvent dtde) {}
//            public void dropActionChanged(DropTargetDragEvent dtde) {}
//            public void dragExit(DropTargetEvent dte) {}
//
//            public void drop(DropTargetDropEvent dtde) {
//                try {
//                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
//                    Transferable transferable = dtde.getTransferable();
//                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
//                    for (DataFlavor flavor : flavors) {
//                        if (flavor.isFlavorJavaFileListType()) {
//                            java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavor);
//                            for (File file : files) {
//                                ImportFile.importTXT(file);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                FileUtils.clearMemory();
                System.exit(0);
            }
        });

        FileUtils.verifyExistingFilesToInitialize();

    }

    private void resetAnyAction() {

        graph.removeCells(new Object[]{this.ghostCell}, true);

        this.ghostCell = null;

        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE) {
            graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            CellUtils.removeCell(this.invisibleCellReference);
        }

        this.setCurrentActionToNone();

        resetEdge();

    }

    @Override
    public void actionPerformed(ActionEvent event) {

        resetAnyAction();

        Button<?> clickedButton = this.buttons
                .stream()
                .filter(button -> button.getButton() == event.getSource())
                .findAny()
                .orElse(null);

        String style = "";

        if (clickedButton != null) {

            resetEdge();

            clickedButton.setCurrentAction(this.currentActionReference);

            switch (this.currentActionReference.get().getType()) {
                case DELETE_CELL ->
                    this.executeRemoveCellCommand(this.jCell);
                case DELETE_ALL ->
                    CellUtils.deleteGraph();
                case PRINT_SCREEN ->
                    this.printScreen();
                case SHOW_CELL ->
                    CellUtils.showTable(this.jCell);
                case IMPORT_FILE ->
                    this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
                case CREATE_TABLE_CELL ->
                    this.createNewTable(CurrentAction.ActionType.CREATE_TABLE_CELL);

                case OPEN_CONNECTIONS ->
                    this.openConnections();
                case OPEN_CONSOLE ->
                    this.openConsole();
                case OPEN_TEXT_EDITOR ->
                    this.changeScreen();
                case OPEN_COMPARATOR -> {
                    try {
                        this.openComparator();
                    } catch (Exception ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (clickedButton instanceof OperationButton clickedOperationButton) {
                style = clickedOperationButton.getStyle();
                ((CreateOperationCellAction) this.currentActionReference.get()).setParent(null);
            }
        }

        try {
            this.onBottomMenuItemClicked(event, clickedButton, style);
            this.onTopMenuBarItemClicked(event);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        resetEdge();
    }

    private void resetEdge() {

        currentEdgeReference.set(new Edge());

    }

    private void onTopMenuBarItemClicked(ActionEvent event) throws Exception {
        Object source = event.getSource();
        String theme = null;

//        if (source == this.importTableTopMenuBarItem) {
//            this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
//        } else 
        if (source == this.openCSVTableTopMenuBarItem) {
            openCSV();
        } else if (source == this.openBTreeTableTopMenuBarItem) {
            new ImportFile(FileType.DAT, new AtomicReference<>(false));
        } else if (source == this.openHeadFileTableTopMenuBarItem) {
            new ImportFile(FileType.HEADER, new AtomicReference<>(false));

        } else if (source == this.openQueryTopMenuBarItem) {
            new ImportFile(FileType.TXT, new AtomicReference<>(false));
        } else if (source == this.gtkThemeTopMenuBarItem) {
            theme = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        } else if (source == this.motifThemeTopMenuBarItem) {
            theme = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        } else if (source == this.nimbusThemeTopMenuBarItem) {
            theme = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
//        } else if (source == this.undoTopMenuBarItem) {
//            commandController.undo();
//        } else if (source == this.redoTopMenuBarItem) {
//            commandController.redo();
        }

        if (theme == null) {
            return;
        }

        try {
            UIManager.setLookAndFeel(theme);
            this.refreshAllComponents();
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException exception) {
            new ErrorFrame(ConstantController.getString("error"));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {

        this.jCell = (mxCell) MainFrame.getGraphComponent().getCellAt(event.getX(), event.getY());

//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException ex) {
//        }
        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.jCell);

        if (optionalCell.isPresent() && SwingUtilities.isRightMouseButton(event)) {
            Cell cell = optionalCell.get();

            this.popupMenuJCell.add(this.runQueryMenuItem);
            this.popupMenuJCell.add(this.informationsMenuItem);
            this.popupMenuJCell.add(this.exportTableMenuItem);
            //this.popupMenuJCell.add(this.generateFyiTableMenuItem);
            this.popupMenuJCell.add(this.renameOperatorMenuItem);
            this.popupMenuJCell.add(this.saveQueryMenuItem);
            this.popupMenuJCell.add(this.editMenuItem);
            this.popupMenuJCell.add(this.operationsMenuItem);
            this.popupMenuJCell.add(this.removeMenuItem);
            this.popupMenuJCell.add(cell.isMarked() ? this.unmarkCellMenuItem : this.markCellMenuItem);
            this.popupMenuJCell.remove(cell.isMarked() ? this.markCellMenuItem : this.unmarkCellMenuItem);

            if (cell.getTree().getCells().stream().anyMatch(Cell::isMarked) && !cell.isMarked()) {

                this.popupMenuJCell.remove(unmarkCellMenuItem);
                this.popupMenuJCell.remove(markCellMenuItem);

            }

//            if (cell.isOperationCell() && !cell.hasSingleSource()) {
//                this.popupMenuJCell.remove(this.renameOperatorMenuItem);
//            }

            if (cell instanceof OperationCell operationCell && !operationCell.hasBeenInitialized()) {
                this.popupMenuJCell.remove(this.runQueryMenuItem);
                this.popupMenuJCell.remove(this.operationsMenuItem);
                //this.popupMenuJCell.remove(this.editMenuItem);
                this.popupMenuJCell.remove(this.exportTableMenuItem);
                this.popupMenuJCell.remove(this.saveQueryMenuItem);
            }

            if (cell.isTableCell()) {
                this.popupMenuJCell.remove(this.editMenuItem);
            }

            if (cell instanceof OperationCell operationCell
                    && OperationType.OPERATIONS_WITHOUT_FORM.contains((operationCell).getType())) {
                this.popupMenuJCell.remove(this.editMenuItem);
            }

            if (cell.hasChild()) {
                this.popupMenuJCell.remove(this.operationsMenuItem);
            }

            if (cell.hasError()) {
                this.popupMenuJCell.remove(this.runQueryMenuItem);
                this.popupMenuJCell.remove(this.operationsMenuItem);

                if (!cell.hasParents()) {
                    this.popupMenuJCell.remove(this.editMenuItem);
                }
            }

            this.popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), event.getX(), event.getY());
        }

        if (optionalCell.isPresent() || this.ghostCell != null) {
            this.executeInsertOperationCellCommand(event);
        }

        if (optionalCell.isPresent() && event.getClickCount() == 2) {
            CellUtils.showTable(this.jCell);
        }
    }

    public static void executeImportTableCommand(TableCell tableCell) {
        commandController.execute(new ImportTableCommand(tableCell));
    }

    public void executeInsertTableCellCommand(mxCell jCell, mxCell ghostCell) {
        UndoableRedoableCommand command = new InsertTableCellCommand(
                new AtomicReference<>(jCell), new AtomicReference<>(ghostCell)
        );

        commandController.execute(command);
    }

    public void executeInsertOperationCellCommand(MouseEvent event) {
        UndoableRedoableCommand command = new InsertOperationCellCommand(
                event, new AtomicReference<>(this.jCell), this.invisibleCellReference,
                new AtomicReference<>(this.ghostCell), new AtomicReference<>(currentEdgeReference.get()),
                this.currentActionReference
        );

        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE && !currentEdgeReference.get().hasParent()
                && !CellUtils.getActiveCell(jCell).get().canBeParent()) {
            return;
        }

        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE
                && this.invisibleCellReference.get() == null) {
            command.execute();
        } else if (this.currentActionReference.get().getType() != ActionType.NONE) {
            commandController.execute(command);
        }
    }

    public void executeRemoveCellCommand(mxCell jCell) {
        commandController.execute(new RemoveCellCommand(new AtomicReference<>(jCell)));
    }

    public static void resetCurrentEdgeReferenceValue(Edge edge) {
        currentEdgeReference.set(edge);
    }

    public static void resetCurrentEdgeReferenceValue() {
        resetCurrentEdgeReferenceValue(new Edge());
    }

    private void executeAsOperator(mxCell cell) {

//        if (CellUtils.getActiveCell(cell).isEmpty()
//                || !(CellUtils.getActiveCell(cell).get().isTableCell()
//                || CellUtils.getActiveCell(cell).get().hasSingleSource()
//                )
//                ) {
//            return;
//        }

        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        AsOperatorForm form = new AsOperatorForm(cancelService);

        if (!cancelService.get()) {
            executeAsOperator(cell, form.getNewName());
        }
    }

    public static void executeAsOperator(mxCell cell, String text) {
//        if (CellUtils.getActiveCell(cell).isEmpty()
//                || !(CellUtils.getActiveCell(cell).get().isTableCell()
//                || CellUtils.getActiveCell(cell).get().hasSingleSource()
//                )) {
//            return;
//        }
        Cell cell_ = CellUtils.getActiveCell(cell).get();
        
        if (cell_ instanceof TableCell)
            ((TableCell)cell_ ).asOperator(text);
        else{
            ((OperationCell)cell_ ).asOperator(text);
        }
    }

    public void onBottomMenuItemClicked(ActionEvent event, Button<?> clickedButton, String style) throws Exception {
        CreateOperationCellAction createOperationAction = null;

        Object menuItem = event.getSource();

        if (menuItem == this.runQueryMenuItem) {
            CellUtils.showTable(this.jCell);
        } else if (menuItem == this.informationsMenuItem) {
            new CellInformationFrame(this.jCell);
        } else if (menuItem == this.renameOperatorMenuItem) {
            executeAsOperator(jCell);
        } else if (menuItem == this.exportTableMenuItem) {
            this.export();
//        } else if (menuItem == this.generateFyiTableMenuItem) {
//            this.generateFyiTableCell();
        } else if (menuItem == this.saveQueryMenuItem) {
            CellUtils
                    .getActiveCell(this.jCell)
                    .ifPresent(cell -> new ExportFile().exportToDsl(cell.getTree()));
        } else if (menuItem == this.editMenuItem) {
            Optional<Cell> activeCell = CellUtils.getActiveCell(this.jCell);
            if (activeCell.isPresent()) {
                Cell cell = activeCell.get();
                OperationCell operationCell = (OperationCell) cell;
                operationCell.editOperation(this.jCell);
                TreeUtils.recalculateContent(operationCell);
            }
        } else if (menuItem == this.removeMenuItem) {
            if (this.jCell != null) {
                this.executeRemoveCellCommand(this.jCell);
            }
            this.resetAnyAction();
        } else if (menuItem == this.markCellMenuItem) {
            CellUtils.markCell(this.jCell);
        } else if (menuItem == this.unmarkCellMenuItem) {
            CellUtils.unmarkCell(this.jCell);
        } else if (menuItem == this.selectionMenuItem) {
            createOperationAction = OperationType.FILTER.getAction();
            style = OperationType.FILTER.displayName;
        } else if (menuItem == this.projectionMenuItem) {
            createOperationAction = OperationType.PROJECTION.getAction();
            style = OperationType.PROJECTION.displayName;
        } else if (menuItem == this.joinMenuItem) {
            createOperationAction = OperationType.JOIN.getAction();
            style = OperationType.JOIN.displayName;
        } else if (menuItem == this.leftJoinMenuItem) {
            createOperationAction = OperationType.LEFT_OUTER_JOIN.getAction();
            style = OperationType.LEFT_OUTER_JOIN.displayName;
        } else if (menuItem == this.rightJoinMenuItem) {
            createOperationAction = OperationType.RIGHT_OUTER_JOIN.getAction();
            style = OperationType.RIGHT_OUTER_JOIN.displayName;
        } else if (menuItem == this.cartesianProductMenuItem) {
            createOperationAction = OperationType.CARTESIAN_PRODUCT.getAction();
            style = OperationType.CARTESIAN_PRODUCT.displayName;
        } else if (menuItem == this.unionMenuItem) {
            createOperationAction = OperationType.UNION.getAction();
            style = OperationType.UNION.displayName;
        } else if (menuItem == this.intersectionMenuItem) {
            createOperationAction = OperationType.INTERSECTION.getAction();
            style = OperationType.INTERSECTION.displayName;
        } else if (menuItem == this.sortMenuItem) {
            createOperationAction = OperationType.SORT.getAction();
            style = OperationType.SORT.displayName;
        }
//         else if (this.indexerMenuItem == menuItem) {
//            createOperationAction = OperationType.INDEXER.getAction();
//            style = OperationType.INDEXER.displayName;
//        }

        if (createOperationAction != null) {
            createOperationAction.setParent(this.jCell);
            this.currentActionReference.set(createOperationAction);
        }

        if (createOperationAction != null || (clickedButton != null && this.currentActionReference.get().getType() == ActionType.CREATE_OPERATOR_CELL)) {
            this.ghostCell = (mxCell) graph.insertVertex(
                    graph.getDefaultParent(), "ghost", style,
                    MouseInfo.getPointerInfo().getLocation().getX() - MainFrame.getGraphComponent().getWidth(),
                    MouseInfo.getPointerInfo().getLocation().getY() - MainFrame.getGraphComponent().getHeight(),
                    80, 30, style
            );
        }
    }

    //not used
    private void generateFyiTableCell() throws Exception {

        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        PKAndNameChooserForm pk = new PKAndNameChooserForm(CellUtils.getActiveCell(this.jCell).orElseThrow());
        List<Column> primaryKeyColumns = pk.getSelectedColumns();

        Cell cell = CellUtils.getActiveCell(this.jCell).get();

        if (!cancelService.get() && !primaryKeyColumns.isEmpty()) {

            List<Column> columns = new ArrayList<>();

            for (Column pkColumns : primaryKeyColumns) {
                columns.add(new Column(cell.getColumns().stream()
                        .filter(c -> c.getSourceAndName()
                        .equalsIgnoreCase((primaryKeyColumns.stream()
                                .filter(x -> x.getSourceAndName()
                                .equalsIgnoreCase(pkColumns.getSourceAndName()))
                                .findFirst().orElseThrow()).getSourceAndName())).findFirst().orElseThrow(), true));
            }

            for (Column c : cell.getColumns()) {

                if (columns.stream().anyMatch(x -> x.getSourceAndName().equalsIgnoreCase(c.getSourceAndName()))) {
                    continue;
                }

                columns.add(new Column(c, false));

            }

            try {

                TableCell tableCell = TableCreator.createIndex(pk.getTableName(), columns, cell, false);
                this.executeImportTableCommand(tableCell);
                CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());

            } catch (DataBaseException e) {
                cancelService.set(true);
                new ErrorFrame(e.getMessage());
            }

        }

    }

    private void openConnections() {
        if (connectionsFrame == null) {
            connectionsFrame = new ConnectionsFrame();
            return;
        }
        connectionsFrame.setLocationRelativeTo(null);
        connectionsFrame.setExtendedState(Frame.NORMAL);
        connectionsFrame.toFront();
    }

    private void openConsole() {

        if (consoleFrame == null) {
            consoleFrame = new ConsoleFrame();
            return;
        }

        consoleFrame.setLocationRelativeTo(null);
        consoleFrame.setExtendedState(Frame.NORMAL);
        consoleFrame.toFront();
    }

    private void openComparator() {
        if (comparatorFrame == null) {
            comparatorFrame = new ComparatorFrame();
            return;
        }

        comparatorFrame.setLocationRelativeTo(null);
        comparatorFrame.setExtendedState(Frame.NORMAL);
        comparatorFrame.toFront();
    }

    private void changeScreen() {
        this.setContentPane(this.textEditor);
        this.revalidate();
    }

    private void createNewTable(CurrentAction.ActionType action) {
        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

        TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE_CELL
                ? new FormFrameCreateTable(cancelServiceReference).getResult()
                : new ImportAsForm(cancelServiceReference).getResult();

        if (!cancelServiceReference.get()) {
            this.executeImportTableCommand(tableCell);
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
        } else {
            if (tableCell != null) {
                TreeUtils.deleteTree(tableCell.getTree());
            }
        }

        this.setCurrentActionToNone();
    }

    private void openCSV() {
        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);
        JFileChooser fileUpload = new JFileChooser();
        fileUpload.setFileFilter(ImportFile.getFileNameExtensionFilter(FileType.CSV));
        if (fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            MainController.setLastDirectory(new File(fileUpload.getCurrentDirectory().getAbsolutePath()));
            File file = fileUpload.getSelectedFile();
            AtomicReference<Boolean> exitReference = new AtomicReference();
            CSVInfo info = new CSVRecognizerForm(
                    Path.of(file.getAbsolutePath()), exitReference
            ).getCSVInfo();
            TableCell tableCell = TableCreator.createCSVTable(
                    info.tableName(), info.columns(), info, false
            );
            MainController.executeImportTableCommand(tableCell);
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
        }
        this.setCurrentActionToNone();

    }

    private void setCurrentActionToNone() {
        this.currentActionReference.set(ConstantController.NONE_ACTION);
    }

    private void export() {
        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        new ExportAsForm(this.jCell, cancelService);
    }

    public static void saveTable(TableCell tableCell) {
        String tableName = tableCell.getName();
        boolean shouldCreateTable = tables.keySet().stream().noneMatch(x -> x.equals(tableName));

        if (!shouldCreateTable) {
            return;
        }

        tablesGraph.insertVertex(
                tablesGraph.getDefaultParent(), null, tableName, 0,
                currentTableYPosition, tableCell.getWidth(), tableCell.getHeight(), tableCell.getStyle()
        );

        tables.put(tableName, tableCell);

        tablesPanel.revalidate();

        currentTableYPosition += 40;
    }

    private void printScreen() {
        saveSelectedNodesAsImage(graph);//new ExportFile();
    }

    private void saveSelectedNodesAsImage(mxGraph graph) {
        // Get the selected cells (nodes) in the graph
        Object[] selectedCells = graph.getSelectionCells();
        if (selectedCells.length == 0) {
            System.out.println("No selected nodes to save.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle(ConstantController.getString("exportFile.saveFile"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(MainController.getLastDirectory());

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));

        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        try {
            Robot robot = new Robot();
            Point locationOnScreen = getLocationOnScreen();
            Rectangle screenRect = new Rectangle(locationOnScreen.x + selectionRectangle.x, locationOnScreen.y + selectionRectangle.y,
                    selectionRectangle.width, selectionRectangle.height);
            BufferedImage screenImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenImage, "png", new File(filePath));
            System.out.println("Screenshot saved successfully.");
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.VK_S) {
            if (this.jCell != null) {
                CellUtils.showTable(this.jCell);
            }
            resetAnyAction();
        } else if (keyCode == KeyEvent.VK_DELETE) {

            graph.getModel().beginUpdate();
            try {
                Object[] cells = graph.getSelectionCells();
                if (cells != null && cells.length > 0) {
                    for (Object cell : cells) {
                        if (cell instanceof mxCell) {
                            this.executeRemoveCellCommand((mxCell) cell);
                        }
                    }
                    //graph.removeCells(cells);
                }
            } finally {
                graph.getModel().endUpdate();
            }

//            if (this.jCell != null) {
//                this.executeRemoveCellCommand(this.jCell);
//            }
            this.resetAnyAction();
        } else if (keyCode == KeyEvent.VK_E) {
            edgeAction();
        } else if (keyCode == KeyEvent.VK_I) {
            this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
            resetAnyAction();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            this.resetAnyAction();
        } else if (keyCode == KeyEvent.VK_L) {
            System.out.println("--------------------------");
            System.out.println("Árvores: ");

            for (Integer key : trees.keySet()) {
                System.out.println(trees.get(key));
            }

            System.out.print("\n\n");
        } else if (keyCode == KeyEvent.VK_A) {
            if (this.jCell != null && CellUtils.getActiveCell(this.jCell).isPresent()) {
                CellUtils.getActiveCell(this.jCell).get().getTree().getTreeLayer();
            }
//        } else if (keyCode == KeyEvent.VK_Z && (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
//            commandController.undo();
//        } else if (keyCode == KeyEvent.VK_Y && (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
//            commandController.redo();
        } else if (keyCode == KeyEvent.VK_M) {
        }

    }

    private void edgeAction() {
        resetEdge();
        setEdgeCursor();
        this.currentActionReference.set(new CurrentAction(CurrentAction.ActionType.CREATE_EDGE));
    }

    private void setEdgeCursor() {
        graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void moveCell2(MouseEvent event, mxCell cellMoved) {

        graph.getModel().beginUpdate();
        try {
            mxGeometry geometry = cellMoved.getGeometry();
            geometry.setTerminalPoint(new mxPoint(event.getX(), event.getY()), false);
            graph.getModel().setGeometry(cellMoved, geometry);
        } finally {
            graph.getModel().endUpdate();
        }

    }

    private void moveCell(MouseEvent event, mxCell cellMoved) {
        int spaceBetweenCursorY = 20;

        mxGeometry geo = cellMoved.getGeometry();

        if (cellMoved.getEdgeAt(0) != null && cellMoved.getEdgeAt(0).getTerminal(true).getGeometry().getCenterY() < event.getY()) {
            spaceBetweenCursorY *= -1;
        }

        double dx = event.getX() - geo.getCenterX();
        double dy = event.getY() - geo.getCenterY() + spaceBetweenCursorY;

        MainFrame.getGraph().moveCells(new Object[]{cellMoved}, dx, dy);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        ActionType currentActionType = this.currentActionReference.get().getType();

        if (currentActionType == ActionType.NONE) {
            return;
        }

        if (currentActionType == ActionType.CREATE_OPERATOR_CELL && this.ghostCell != null) {
            this.moveCell(event, this.ghostCell);
        } else if (currentActionType == ActionType.CREATE_EDGE && this.invisibleCellReference.get() != null) {
            this.moveCell(event, this.invisibleCellReference.get());
        } else if (this.currentActionReference.get() instanceof CreateTableCellAction createTable) {
            this.moveCell(event, createTable.getTableCell().getJCell());
        } else if (currentActionType == ActionType.CREATE_EDGE) {
            this.setEdgeCursor();
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {

        //        startX = event.getX();
        //        startY = event.getY();
    }

    @Override
    public void mouseDragged(MouseEvent event) {

        //        int dx = event.getX() - startX;
        //        int dy = event.getY() - startY;
        //
        //        int viewX = graphComponent.getViewport().getView().getX();
        //        int viewY = graphComponent.getViewport().getView().getY();
        //
        //        graphComponent.getViewport().setViewPosition(new java.awt.Point(viewX - dx, viewY - dy));
        //
        //        startX = event.getX();
        //        startY = event.getY();
    }

    public static Map<Integer, Tree> getTrees() {
        return trees;
    }

    public static Map<String, TableCell> getTables() {
        return tables;
    }

    public static File getLastDirectory() {
        return lastDirectory;
    }

    public static void setLastDirectory(File lastDirectory) {
        MainController.lastDirectory = lastDirectory;
    }

    public static void putTableCell(Relation relation) {
        int x, y;

        if (relation.getCoordinates().isPresent()) {
            x = relation.getCoordinates().get().x();
            y = relation.getCoordinates().get().y();
        } else {
            x = (int) RandomUtils.nextDouble() * 600;
            y = (int) RandomUtils.nextDouble() * 600;
        }

        TableCell tableCell = MainController.getTables().get(relation.getFirstName());

        CellType cellType = CellType.fromTableCell(tableCell);

        mxCell jTableCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                        graph.getDefaultParent(), null, relation.getFirstName(), x, y,
                        ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT,
                        cellType.id);

        relation.setCell(
                switch (cellType) {
            case FYI_TABLE ->
                new FYITableCell((FYITableCell) tableCell, jTableCell);
            case CSV_TABLE ->
                new CSVTableCell((CSVTableCell) tableCell, jTableCell);
            case MEMORY_TABLE ->
                new MemoryTableCell((MemoryTableCell) tableCell, jTableCell);
            default ->
                throw new RuntimeException();
        });

        try {
            relation.getCell().getTable().open();
        } catch (Exception ex) {
        }

        saveTable(relation.getCell());

        if (!relation.getFirstName().equals(relation.getName())) {
            executeAsOperator(jTableCell, relation.getName());
        }

    }

    public static void putOperationCell(OperationExpression operationExpression) {
        int x, y;

        if (operationExpression.getCoordinates().isPresent()) {
            x = operationExpression.getCoordinates().get().x();
            y = operationExpression.getCoordinates().get().y();
        } else {
            x = RandomUtils.nextInt(0, 1) * 600;
            y = RandomUtils.nextInt(0, 1) * 600;
        }

        OperationType type = operationExpression.getType();

        int width = CellUtils.getCellWidth(type.getFormattedDisplayName());
        mxCell jCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                        graph.getDefaultParent(), null, type.getFormattedDisplayName(),
                        x, y, width, 30, CellType.OPERATION.id
                );

        List<Cell> parents = new ArrayList<>();

        if (operationExpression.getSource()!=null)
        {
            parents.addAll(List.of(operationExpression.getSource().getCell()));
        }
        if (operationExpression instanceof BinaryExpression binaryExpression) {
            parents.add(binaryExpression.getSource2().getCell());
        }

        operationExpression.setCell(new OperationCell(jCell, type, parents, operationExpression.getArguments(), operationExpression.getAlias()));

        OperationCell cell = operationExpression.getCell();

        cell.setAllNewTrees();
    }

    public static int getCurrentTableYPosition() {
        return currentTableYPosition;
    }

    public static void incrementCurrentTableYPosition(int offset) {
        currentTableYPosition += offset;
    }

    public static void decrementCurrentTableYPosition(int offset) {
        currentTableYPosition = Math.max(currentTableYPosition - offset, 0);
    }

}
