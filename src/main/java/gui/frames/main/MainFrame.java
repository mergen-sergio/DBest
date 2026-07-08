package gui.frames.main;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import controllers.ConstantController;
import entities.Action.CurrentAction;
import enums.CellType;
import enums.OperationType;
import files.FileUtils;
import gui.frames.CustomGraphComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import gui.palette.MxStyles;
import gui.palette.OperatorPalette;
import gui.palette.ViewMode;
import gui.theme.Theme;
import gui.theme.ThemeVariant;
import gui.theme.Themed;
import gui.components.IconButton;
import controllers.MainController;

public abstract class MainFrame extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

    private static Container mainContainer;

    protected static mxGraph graph;

    protected static CustomGraphComponent graphComponent;



    protected OperatorPalette operatorPalette;

    protected static mxGraph tablesGraph;

    protected mxGraphComponent tablesComponent;

    protected static JPanel tablesPanel;

    protected JToolBar toolBar;


    protected JPopupMenu tablesPopupMenu;

    protected JMenuItem removeTableMenuItem;

    protected JMenuItem renameTableMenuItem;

    protected JPopupMenu popupMenuJCell;

    protected JMenu operationsMenuItem;

    protected JMenuItem runQueryMenuItem;

    protected JMenuItem informationsMenuItem;

    protected JMenuItem renameOperatorMenuItem;

    protected JMenuItem exportTableMenuItem;

    //protected JMenuItem generateFyiTableMenuItem;

    protected JMenuItem saveQueryMenuItem;

    protected JMenuItem saveQueryAsImageMenuItem;

    protected JMenuItem editMenuItem;

    protected JMenuItem removeMenuItem;

    protected JMenuItem markCellMenuItem;

    protected JMenuItem unmarkCellMenuItem;

    protected JMenuItem copyMenuItem;

    protected JMenuItem pasteMenuItem;

    protected JMenuItem redistributeNodesMenuItem;

    protected JMenuItem swapBinarySidesMenuItem;

    protected JMenu replaceJoinMenuItem;

    protected JMenuItem selectionMenuItem;

    protected JMenuItem projectionMenuItem;

    protected JMenuItem filterColumnMenuItem;

    protected JMenuItem sortMenuItem;

    protected JMenuItem aggregationMenuItem;

    protected JMenuItem groupMenuItem;

    protected JMenuItem renameMenuItem;

//    protected JMenuItem indexerMenuItem;

    protected JMenuItem joinMenuItem;

    protected JMenuItem semiJoinMenuItem;

    protected JMenuItem leftJoinMenuItem;

    protected JMenuItem rightJoinMenuItem;

    protected JMenuItem cartesianProductMenuItem;

    protected JMenuItem unionMenuItem;

    protected JMenuItem intersectionMenuItem;

    protected JMenuItem differenceMenuItem;

    //protected JMenuItem importTableMenuItem;

    //protected JMenuItem openCSVTableMenuItem;

    //protected JMenuItem openHeadFileMenuItem;

    //protected JMenuItem openBTreeTableMenuItem;

    // protected JMenuItem importTreeMenuItem;

    protected JMenuBar topMenuBar = new JMenuBar();

    //protected JMenuItem importTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.importTable"));

    protected JMenuItem openDatabaseConnectionTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openDatabaseConnection"));

    protected JMenuItem openCSVTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openCSVTable"));

    protected JMenuItem openBTreeTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openBTreeTable"));

    protected JMenuItem openHeadFileTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openHeadFileTable"));

    protected JMenuItem openQueryTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openQuery"));

    protected JButton undoButton = new IconButton(null,
            org.kordamp.ikonli.materialdesign2.MaterialDesignA.ARROW_LEFT,
            IconButton.Variant.QUIET);

    protected JButton redoButton = new IconButton(null,
            org.kordamp.ikonli.materialdesign2.MaterialDesignA.ARROW_RIGHT,
            IconButton.Variant.QUIET);

    protected MainFrame() {
        super(ConstantController.APPLICATION_TITLE);

        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        }catch (Exception ignored){
        }

        this.initializeFields();
        this.initializeGUI();
    }

    private void initializeFields() {
        graph = new mxGraph();
        graphComponent = new CustomGraphComponent(graph);
        this.operatorPalette = new OperatorPalette();
        tablesGraph = new mxGraph();
        this.tablesComponent = new mxGraphComponent(tablesGraph);
        tablesPanel = new JPanel();
        this.toolBar = new JToolBar();
        this.popupMenuJCell = new JPopupMenu();
        this.tablesPopupMenu = new JPopupMenu();

        this.topMenuBar = new JMenuBar();
        this.runQueryMenuItem = new JMenuItem(ConstantController.getString("cell.runQuery"));
        this.informationsMenuItem = new JMenuItem(ConstantController.getString("cell.informations"));
        this.renameOperatorMenuItem = new JMenuItem(ConstantController.getString("cell.rename"));
        this.exportTableMenuItem = new JMenuItem(ConstantController.getString("cell.exportTable"));
        //this.generateFyiTableMenuItem = new JMenuItem(ConstantController.getString("cell.generateFyiTable"));
        this.saveQueryMenuItem = new JMenuItem(ConstantController.getString("cell.saveQuery"));
        this.saveQueryAsImageMenuItem = new JMenuItem(ConstantController.getString("cell.saveQueryAsImage"));
        this.editMenuItem = new JMenuItem(ConstantController.getString("cell.edit"));
        this.removeMenuItem = new JMenuItem(ConstantController.getString("cell.remove"));
        this.markCellMenuItem = new JMenuItem(ConstantController.getString("cell.mark"));
        this.unmarkCellMenuItem = new JMenuItem(ConstantController.getString("cell.unmark"));
        this.copyMenuItem = new JMenuItem("Copy");
        this.pasteMenuItem = new JMenuItem("Paste");
        this.redistributeNodesMenuItem = new JMenuItem("Redistribute Nodes");
        this.swapBinarySidesMenuItem = new JMenuItem("Swap Edges");
        this.replaceJoinMenuItem = new JMenu("Replace Join");
        this.removeTableMenuItem = new JMenuItem("Remove Table");
        this.renameTableMenuItem = new JMenuItem("Rename Table");
        this.operationsMenuItem = new JMenu(ConstantController.getString("cell.operations"));
        this.selectionMenuItem = new JMenuItem(OperationType.FILTER.displayName);
        this.projectionMenuItem = new JMenuItem(OperationType.PROJECTION.displayName);
        this.filterColumnMenuItem = new JMenuItem(OperationType.SELECT_COLUMNS.displayName);
        this.sortMenuItem = new JMenuItem(OperationType.SORT.displayName);
        this.aggregationMenuItem = new JMenuItem(OperationType.AGGREGATION.displayName);
        this.groupMenuItem = new JMenuItem(OperationType.GROUP.displayName);
        this.renameMenuItem = new JMenuItem(OperationType.RENAME.displayName);
//        this.indexerMenuItem = new JMenuItem(OperationType.INDEXER.displayName);
        this.joinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_JOIN.displayName);
        this.semiJoinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_LEFT_SEMI_JOIN.displayName);
        this.leftJoinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_LEFT_OUTER_JOIN.displayName);
        this.rightJoinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_RIGHT_OUTER_JOIN.displayName);
        this.cartesianProductMenuItem = new JMenuItem(OperationType.CARTESIAN_PRODUCT.displayName);
        this.unionMenuItem = new JMenuItem(OperationType.UNION.displayName);
        this.intersectionMenuItem = new JMenuItem(OperationType.INTERSECTION.displayName);
        this.differenceMenuItem = new JMenuItem(OperationType.DIFFERENCE.displayName);
//        this.importTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.importTable"));
//        this.openCSVTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.openCSVTable"));
//        this.openBTreeTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.openBTreeTable"));
//        this.openHeadFileMenuItem = new JMenuItem(ConstantController.getString("menu.file.openHeadFile"));
          //this.importTreeMenuItem = new JMenuItem(ConstantController.getString("menu.file.importTree"));
    }

    private void initializeGUI() {
        this.setSize(ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);




        tablesPanel.add(this.tablesComponent, BorderLayout.CENTER);

        this.getContentPane().add(this.topMenuBar, BorderLayout.NORTH);
        this.getContentPane().add(graphComponent, BorderLayout.CENTER);

        Runnable applyCanvasBackground = () -> {
            graphComponent.setBackground(Theme.BACKGROUND);
            graphComponent.getViewport().setBackground(Theme.BACKGROUND);
        };
        applyCanvasBackground.run();
        Theme.addChangeListener(applyCanvasBackground);
        this.getContentPane().add(tablesPanel, BorderLayout.WEST);

        tablesPanel.setOpaque(true);
        Themed.background(tablesPanel, () -> Theme.SURFACE);
        Themed.background(this.tablesComponent, () -> Theme.SURFACE);
        this.tablesComponent.getViewport().setOpaque(true);
        Themed.background(this.tablesComponent.getViewport(), () -> Theme.SURFACE);

        //this.tablesPanel.setPreferredSize(new Dimension(0, 0));
        this.getContentPane().add(this.operatorPalette, BorderLayout.EAST);

        MxStyles.registerOperatorCellStyles(graph);

        Theme.addChangeListener(() -> {
            MxStyles.registerDefaultEdgeStyle(graph);

            setOperationCellStyle();
            graphComponent.refresh();
        });
        this.getContentPane().add(this.toolBar, BorderLayout.SOUTH);

        Themed.background(this.toolBar, () -> Theme.SURFACE);
        Themed.background(this.topMenuBar, () -> Theme.SURFACE);
        Themed.foreground(this.topMenuBar, () -> Theme.TEXT_PRIMARY);
        this.toolBar.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_SMALL, Theme.SPACING_SMALL,
            Theme.SPACING_SMALL, Theme.SPACING_SMALL));
        this.toolBar.setOpaque(true);
        this.topMenuBar.setOpaque(true);

        this.topMenuBar.setBorder(BorderFactory.createEmptyBorder());
        this.graphComponent.setBorder(BorderFactory.createEmptyBorder());
        this.tablesPanel.setBorder(BorderFactory.createEmptyBorder());
        this.tablesComponent.setBorder(BorderFactory.createEmptyBorder());



        this.addBottomButtons();
        this.addTopMenuBarFileItems();
        this.addTopMenuBarAppearanceItems();
        this.addTopMenuBarEditItems();

        this.getContentPane().addKeyListener(this);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setUseBoundingBox(false);

        this.setTablesSavedGraphConfig();
        this.setGraphConfig();

        this.setMenuItemsListener();

        this.tablesPopupMenu.add(this.removeTableMenuItem);
        this.tablesPopupMenu.add(this.renameTableMenuItem);

        this.addMenuItemOperations();

        mainContainer = this.getContentPane();

        this.setJCellStyles();
        this.setVisible(true);
    }


    private void addTopMenuBarFileItems() {
        JMenu fileMenu = new JMenu(ConstantController.getString("menu.file"));
        this.topMenuBar.add(fileMenu);

        //fileMenu.add(this.importTableTopMenuBarItem);
        fileMenu.add(this.openDatabaseConnectionTopMenuBarItem);
        fileMenu.add(this.openCSVTableTopMenuBarItem);
        fileMenu.add(this.openBTreeTableTopMenuBarItem);
        fileMenu.add(this.openHeadFileTableTopMenuBarItem);

        fileMenu.add(this.openQueryTopMenuBarItem);

        //this.importTableTopMenuBarItem.addActionListener(this);
        this.openDatabaseConnectionTopMenuBarItem.addActionListener(this);
        this.openCSVTableTopMenuBarItem.addActionListener(this);
        this.openBTreeTableTopMenuBarItem.addActionListener(this);
        this.openHeadFileTableTopMenuBarItem.addActionListener(this);
        this.openQueryTopMenuBarItem.addActionListener(this);
    }

    private void addTopMenuBarAppearanceItems() {
        JMenu appearanceMenu = new JMenu(ConstantController.getString("menu.appearance"));
        this.topMenuBar.add(appearanceMenu);


        JMenu paletteMenu = new JMenu(ConstantController.getString("menu.appearance.palette"));
        appearanceMenu.add(paletteMenu);
        ButtonGroup paletteGroup = new ButtonGroup();
        for (ThemeVariant variant : ThemeVariant.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(variant.getDisplayName());
            item.setSelected(variant == Theme.getActive());
            item.addActionListener(event -> Theme.setActive(variant));
            paletteGroup.add(item);
            paletteMenu.add(item);
        }


        JMenu iconDisplayMenu = new JMenu(
                ConstantController.getString("menu.appearance.iconDisplay"));
        appearanceMenu.add(iconDisplayMenu);
        ButtonGroup iconDisplayGroup = new ButtonGroup();
        for (ViewMode mode : ViewMode.values()) {
            String key = "palette.viewMode." + mode.name().toLowerCase();
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(
                    ConstantController.getString(key));
            item.setSelected(mode == this.operatorPalette.getCurrentViewMode());
            item.addActionListener(event -> this.operatorPalette.setCurrentViewMode(mode));
            iconDisplayGroup.add(item);
            iconDisplayMenu.add(item);
        }
    }

    private void addTopMenuBarEditItems() {
        for (JButton btn : new JButton[]{this.undoButton, this.redoButton}) {
            btn.setPreferredSize(new Dimension(34, 28));
            btn.setMaximumSize(new Dimension(34, 28));
            btn.setFocusPainted(false);
            btn.setEnabled(false);
            btn.addActionListener(this);
            this.topMenuBar.add(btn);
        }
    }

    private void setJCellStyles() {

        setCSVCellStyle();
        setFYICellStyle();
        setXMLCellStyle();
        setOperationCellStyle();
        setMemoryCellStyle();

    }

    private void setCSVCellStyle(){

        Map<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#98FB98");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.CSV_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setFYICellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "#EEEE89");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.FYI_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setXMLCellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "#FFB6C1");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.XML_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setOperationCellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "none");
        style.put(mxConstants.STYLE_STROKECOLOR, "none");

        style.put(mxConstants.STYLE_FONTCOLOR,
                com.mxgraph.util.mxUtils.getHexColorString(Theme.CANVAS_TEXT));

        String customStyle = CellType.OPERATION.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setMemoryCellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "#C3DEE6");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.MEMORY_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }




    private JButton sidebarToggleButton;
    private boolean sidebarVisible = true;

    private void addBottomButtons() {

        MainController controller = (MainController) this;

        addBottomAction(controller, "toolBarButtons.importTable",    "i",    CurrentAction.ActionType.IMPORT_FILE,       IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.createTable",    "c",    CurrentAction.ActionType.CREATE_TABLE_CELL, IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.edge",           "e",    CurrentAction.ActionType.CREATE_EDGE,       IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.remove",         "del",  CurrentAction.ActionType.DELETE_CELL,       IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.removeAll",      null,   CurrentAction.ActionType.DELETE_ALL,        IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.screenshot",     null,   CurrentAction.ActionType.PRINT_SCREEN,      IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.console",        null,   CurrentAction.ActionType.OPEN_CONSOLE,      IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.textEditor",     null,   CurrentAction.ActionType.OPEN_TEXT_EDITOR,  IconButton.Variant.DEFAULT);
        addBottomAction(controller, "toolBarButtons.comparator",     null,   CurrentAction.ActionType.OPEN_COMPARATOR,   IconButton.Variant.DEFAULT);

        JLabel hint = new JLabel(ConstantController.getString("toolBarButtons.dragHint"));
        hint.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        Themed.foreground(hint, () -> Theme.TEXT_MUTED);
        this.toolBar.add(hint);

        this.toolBar.add(javax.swing.Box.createHorizontalGlue());
        this.sidebarToggleButton = buildSidebarToggle();
        this.toolBar.add(this.sidebarToggleButton);
    }


    private void addBottomAction(MainController controller, String labelKey, String shortcut,
                                 CurrentAction.ActionType actionType, IconButton.Variant variant) {
        String label = ConstantController.getString(labelKey);
        if (shortcut != null) label = label + "  (" + shortcut + ")";
        IconButton button = new IconButton(label, null, variant);
        button.addActionListener(event -> controller.dispatchToolBarAction(actionType));

        this.toolBar.add(javax.swing.Box.createHorizontalStrut(Theme.SPACING_MEDIUM));
        this.toolBar.add(button);
    }

    private JButton buildSidebarToggle() {
        IconButton button = new IconButton(null,
                org.kordamp.ikonli.materialdesign2.MaterialDesignC.CHEVRON_RIGHT,
                IconButton.Variant.QUIET);
        button.setToolTipText(ConstantController.getString("toolBarButtons.toggleSidebar"));
        button.addActionListener(event -> toggleOperatorSidebar());
        applySidebarToggleAppearance(button);
        Theme.addChangeListener(() -> applySidebarToggleAppearance(button));
        return button;
    }

    private void applySidebarToggleAppearance(JButton button) {
        java.awt.Color colour = this.sidebarVisible ? Theme.ACCENT : Theme.TEXT_MUTED;
        org.kordamp.ikonli.Ikon icon = this.sidebarVisible
                ? org.kordamp.ikonli.materialdesign2.MaterialDesignC.CHEVRON_RIGHT
                : org.kordamp.ikonli.materialdesign2.MaterialDesignC.CHEVRON_LEFT;
        button.setIcon(org.kordamp.ikonli.swing.FontIcon.of(icon, 18, colour));
        button.repaint();
    }

    private void toggleOperatorSidebar() {
        this.sidebarVisible = !this.sidebarVisible;
        this.operatorPalette.setVisible(this.sidebarVisible);
        applySidebarToggleAppearance(this.sidebarToggleButton);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }

    private void setTablesSavedGraphConfig() {
        this.tablesComponent.getGraphControl().addMouseListener(this);
        this.tablesComponent.setConnectable(false);

        tablesGraph.setAutoSizeCells(false);
        tablesGraph.setCellsResizable(false);
        tablesGraph.setAutoOrigin(false);
        tablesGraph.setCellsEditable(false);
        tablesGraph.setAllowDanglingEdges(false);
        tablesGraph.setCellsMovable(false);
        tablesGraph.setCellsDeletable(false);
    }

    private void setGraphConfig() {
        graphComponent.getGraphControl().addMouseMotionListener(this);
        graphComponent.getGraphControl().addKeyListener(this);
        graphComponent.setConnectable(false);
        graphComponent.getGraphControl().addMouseListener(this);
        graphComponent.addKeyListener(this);
        graphComponent.setFocusable(true);
        //graphComponent.setGridVisible(true);
        graphComponent.setDragEnabled(true);
        //graphComponent.setGridStyle(1);
        graphComponent.setAutoExtend(true);
        graphComponent.requestFocus();
        graphComponent.setComponentPopupMenu(this.popupMenuJCell);

        graph.setAutoSizeCells(false);
        graph.setCellsResizable(false);
        graph.setAutoOrigin(false);
        graph.setCellsEditable(false);
        graph.setAllowDanglingEdges(false);
        graph.setAllowLoops(false);
        graph.setCellsBendable(false);
        graph.setConnectableEdges(false);
        graph.setEdgeLabelsMovable(false);

    }

    private void setMenuItemsListener() {
        this.informationsMenuItem.addActionListener(this);
        this.renameOperatorMenuItem.addActionListener(this);
        this.exportTableMenuItem.addActionListener(this);
        //this.generateFyiTableMenuItem.addActionListener(this);
        this.saveQueryMenuItem.addActionListener(this);
        this.saveQueryAsImageMenuItem.addActionListener(this);
        this.runQueryMenuItem.addActionListener(this);
        this.editMenuItem.addActionListener(this);
        this.removeMenuItem.addActionListener(this);
        this.markCellMenuItem.addActionListener(this);
        this.unmarkCellMenuItem.addActionListener(this);
        this.removeTableMenuItem.addActionListener(this);
        this.renameTableMenuItem.addActionListener(this);
        this.copyMenuItem.addActionListener(this);
        this.pasteMenuItem.addActionListener(this);
        this.redistributeNodesMenuItem.addActionListener(this);
        this.swapBinarySidesMenuItem.addActionListener(this);
        this.selectionMenuItem.addActionListener(this);
        this.projectionMenuItem.addActionListener(this);
        this.filterColumnMenuItem.addActionListener(this);
        this.sortMenuItem.addActionListener(this);
        this.aggregationMenuItem.addActionListener(this);
        this.groupMenuItem.addActionListener(this);
        this.renameMenuItem.addActionListener(this);
//        this.indexerMenuItem.addActionListener(this);
        this.joinMenuItem.addActionListener(this);
        this.leftJoinMenuItem.addActionListener(this);
        this.rightJoinMenuItem.addActionListener(this);
        this.cartesianProductMenuItem.addActionListener(this);
        this.unionMenuItem.addActionListener(this);
        this.intersectionMenuItem.addActionListener(this);
    }

    private void addMenuItemOperations() {
        this.operationsMenuItem.add(this.selectionMenuItem);
        this.operationsMenuItem.add(this.projectionMenuItem);
        this.operationsMenuItem.add(this.filterColumnMenuItem);
        this.operationsMenuItem.add(this.sortMenuItem);
        this.operationsMenuItem.add(this.aggregationMenuItem);
        this.operationsMenuItem.add(this.groupMenuItem);
        this.operationsMenuItem.add(this.renameMenuItem);
//        this.operationsMenuItem.add(this.indexerMenuItem);
        this.operationsMenuItem.addSeparator();
        this.operationsMenuItem.add(this.joinMenuItem);
        this.operationsMenuItem.add(this.leftJoinMenuItem);
        this.operationsMenuItem.add(this.rightJoinMenuItem);
        this.operationsMenuItem.add(this.cartesianProductMenuItem);
        this.operationsMenuItem.add(this.unionMenuItem);
        this.operationsMenuItem.add(this.intersectionMenuItem);
    }

    public static mxGraph getGraph() {
        return graph;
    }

    public static mxGraph getTablesGraph() {
        return tablesGraph;
    }

    public static JPanel getTablesPanel() {
        return tablesPanel;
    }

    public static mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void goBackToMain() {
        this.setContentPane(mainContainer);
        this.revalidate();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
    }

//    int startX, startY;

    @Override
    public void mouseReleased(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

}
