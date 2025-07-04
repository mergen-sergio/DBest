package gui.frames.forms.importexport;

import controllers.ConstantController;
import controllers.MainController;
import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidXMLException;
import files.xml.XMLInfo;
import gui.frames.ErrorFrame;
import gui.frames.forms.FormBase;
import sources.xml.XMLAnalysisResult;
import sources.xml.XMLRecognizer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Form for configuring XML import settings and previewing data
 */
public class XMLRecognizerForm extends FormBase implements ActionListener {

    private final JPanel headerPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();
    private JTable jTable;
    private DefaultTableModel model;

    private final JTextField tableNameTextField = new JTextField();
    private final JTextField rootElementTextField = new JTextField();
    private final JTextField recordElementTextField = new JTextField();
    private final JComboBox<XMLRecognizer.FlatteningStrategy> strategyComboBox = new JComboBox<>(XMLRecognizer.FlatteningStrategy.values());

    private final Map<String, JComboBox<ColumnDataType>> typeComboBoxes = new HashMap<>();
    private final List<String> columnNames = new ArrayList<>();

    private XMLAnalysisResult analysisResult;
    private final Path path;
    private final AtomicReference<Boolean> exitReference;
    private final StringBuilder tableName;
    private final List<Column> columns;

    public XMLRecognizerForm(Path path, AtomicReference<Boolean> exitReference) {
        super(null);

        this.setModal(true);
        this.exitReference = exitReference;
        this.path = path;
        this.columns = new ArrayList<>();
        this.tableName = new StringBuilder();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitReference.set(true);
            }
        });

        this.initGUI();
    }

    public void initGUI() {
        this.setBounds(0, 0, ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("XML Import Configuration");

        try {
            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString());
            this.analysisResult = recognizer.analyzeStructure();
        } catch (InvalidXMLException exception) {
            new ErrorFrame(exception.getMessage());
            this.exitReference.set(true);
            return;
        }

        this.loadJTable();
        this.initializeHeader();
        this.initializeMain();
        this.btnReady.addActionListener(this);
        this.btnCancel.addActionListener(this);
        this.verifyReadyButton();
        this.setVisible(true);
    }

    private void initializeHeader() {
        this.contentPanel.add(this.headerPanel, BorderLayout.NORTH);
        this.headerPanel.setLayout(new BoxLayout(this.headerPanel, BoxLayout.Y_AXIS));

        JPanel itemsPadding = new JPanel();
        itemsPadding.setLayout(new BoxLayout(itemsPadding, BoxLayout.X_AXIS));

        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));

        JPanel itemTableName = new JPanel();
        itemTableName.setLayout(new BoxLayout(itemTableName, BoxLayout.X_AXIS));

        JPanel itemRootElement = new JPanel();
        itemRootElement.setLayout(new BoxLayout(itemRootElement, BoxLayout.X_AXIS));

        JPanel itemRecordElement = new JPanel();
        itemRecordElement.setLayout(new BoxLayout(itemRecordElement, BoxLayout.X_AXIS));

        JPanel itemStrategy = new JPanel();
        itemStrategy.setLayout(new BoxLayout(itemStrategy, BoxLayout.X_AXIS));

        this.headerPanel.add(itemsPadding);
        itemsPadding.add(items);

        items.add(Box.createVerticalStrut(5));
        items.add(itemTableName);
        items.add(itemRootElement);
        items.add(itemRecordElement);
        items.add(itemStrategy);
        items.add(Box.createVerticalStrut(5));

        itemsPadding.add(Box.createHorizontalStrut(10));

        // Table name
        itemTableName.add(new JLabel("Table Name:"));
        itemTableName.add(Box.createHorizontalStrut(10));
        itemTableName.add(this.tableNameTextField);
        itemTableName.add(Box.createHorizontalGlue());

        // Set default table name from file
        String fileName = this.path.getFileName().toString();
        String defaultTableName = fileName.substring(0, fileName.lastIndexOf('.'));
        this.tableNameTextField.setText(defaultTableName);

        // Root element
        itemRootElement.add(new JLabel("Root Element:"));
        itemRootElement.add(Box.createHorizontalStrut(10));
        itemRootElement.add(this.rootElementTextField);
        itemRootElement.add(Box.createHorizontalGlue());

        this.rootElementTextField.setText(this.analysisResult.getRootElement());
        this.rootElementTextField.setToolTipText("Leave empty for auto-detection");

        // Record element
        itemRecordElement.add(new JLabel("Record Element:"));
        itemRecordElement.add(Box.createHorizontalStrut(10));
        itemRecordElement.add(this.recordElementTextField);
        itemRecordElement.add(Box.createHorizontalGlue());

        this.recordElementTextField.setText(this.analysisResult.getRecordElement());
        this.recordElementTextField.setToolTipText("Leave empty for auto-detection");

        // Flattening strategy
        itemStrategy.add(new JLabel("Flattening Strategy:"));
        itemStrategy.add(Box.createHorizontalStrut(10));
        itemStrategy.add(this.strategyComboBox);
        itemStrategy.add(Box.createHorizontalGlue());

        this.strategyComboBox.setSelectedItem(this.analysisResult.getStrategy());
        this.strategyComboBox.addActionListener(this);

        // Add action listeners for text fields
        this.rootElementTextField.addActionListener(this);
        this.recordElementTextField.addActionListener(this);
        this.tableNameTextField.addActionListener(this);
    }

    private void initializeMain() {
        this.contentPanel.add(this.mainPanel, BorderLayout.CENTER);

        this.mainPanel.setLayout(new BorderLayout());
        this.mainPanel.add(this.scrollPane);

        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.scrollPane.getViewport().setBackground(Color.WHITE);
        this.scrollPane.getViewport().setPreferredSize(this.scrollPane.getPreferredSize());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.verifyReadyButton();

        if (event.getSource() == this.strategyComboBox ||
            event.getSource() == this.rootElementTextField ||
            event.getSource() == this.recordElementTextField) {
            this.updateTable();
        }

        if (event.getSource() == this.btnCancel) {
            this.dispose();
            this.exitReference.set(true);
        }

        if (event.getSource() == this.btnReady) {
            this.dispose();
            this.setItems();
        }
    }

    private void verifyReadyButton() {
        boolean tableNameAlreadyExists = MainController.getTables().containsKey(this.tableNameTextField.getText().strip());
        this.btnReady.setEnabled(!tableNameAlreadyExists && !this.tableNameTextField.getText().strip().isEmpty());
    }

    private void setItems() {
        this.tableName.append(this.tableNameTextField.getText().strip());

        for (String columnName : this.columnNames) {
            ColumnDataType type;

            if (this.typeComboBoxes.containsKey(columnName)) {
                type = (ColumnDataType) this.typeComboBoxes.get(columnName).getSelectedItem();
            } else {
                type = ColumnDataType.STRING;
            }

            Column column = new Column(columnName, "xml", type, false, false);
            this.columns.add(column);
        }
    }

    private void updateTable() {
        String rootElement = this.rootElementTextField.getText().trim();
        String recordElement = this.recordElementTextField.getText().trim();
        XMLRecognizer.FlatteningStrategy strategy = (XMLRecognizer.FlatteningStrategy) this.strategyComboBox.getSelectedItem();

        if (rootElement.isEmpty()) rootElement = null;
        if (recordElement.isEmpty()) recordElement = null;

        try {
            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString(), rootElement, recordElement, strategy);
            this.analysisResult = recognizer.analyzeStructure();
        } catch (InvalidXMLException exception) {
            new ErrorFrame(exception.getMessage());
            return;
        }

        this.loadJTable();
        this.verifyReadyButton();
        this.revalidate();
    }

    private void loadJTable() {
        this.columnNames.clear();
        this.typeComboBoxes.clear();

        if (this.analysisResult == null || this.analysisResult.getColumns().isEmpty()) {
            this.model = new DefaultTableModel();
            this.jTable = new JTable(this.model);
            this.scrollPane.setViewportView(this.jTable);
            return;
        }

        // Create column names
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Row");

        for (entities.Column column : this.analysisResult.getColumns()) {
            String columnName = column.NAME;
            columnNames.add(columnName);
            this.columnNames.add(columnName);
        }

        // Create data rows
        Vector<Vector<Object>> data = new Vector<>();

        // Add header row with type selectors
        Vector<Object> headerRow = new Vector<>();
        headerRow.add("Type:");

        for (entities.Column column : this.analysisResult.getColumns()) {
            JComboBox<ColumnDataType> typeComboBox = new JComboBox<>(ColumnDataType.values());
            typeComboBox.setSelectedItem(column.DATA_TYPE);
            this.typeComboBoxes.put(column.NAME, typeComboBox);
            headerRow.add(typeComboBox);
        }
        data.add(headerRow);

        // Add sample data rows
        List<Map<String, String>> sampleData = this.analysisResult.getSampleData();
        for (int i = 0; i < Math.min(10, sampleData.size()); i++) {
            Vector<Object> row = new Vector<>();
            row.add(i + 1);

            Map<String, String> rowData = sampleData.get(i);
            for (entities.Column column : this.analysisResult.getColumns()) {
                String value = rowData.get(column.NAME);
                row.add(value != null ? value : "");
            }
            data.add(row);
        }

        this.model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return row == 0 && column > 0; // Only type selectors are editable
            }
        };

        this.jTable = new JTable(this.model);
        this.jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Configure custom cell renderer and editor for the first row (type selectors)
        for (int col = 1; col < columnNames.size(); col++) {
            String columnName = this.analysisResult.getColumns().get(col - 1).NAME;
            JComboBox<ColumnDataType> comboBox = this.typeComboBoxes.get(columnName);

            // Set custom renderer
            this.jTable.getColumnModel().getColumn(col).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    if (row == 0) {
                        return comboBox;
                    } else {
                        // Use default renderer for data rows
                        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                        return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    }
                }
            });

            // Set custom editor
            this.jTable.getColumnModel().getColumn(col).setCellEditor(new DefaultCellEditor(comboBox));
        }

        this.scrollPane.setViewportView(this.jTable);
    }

    public XMLInfo getXMLInfo() {
        String rootElement = this.rootElementTextField.getText().trim();
        String recordElement = this.recordElementTextField.getText().trim();
        XMLRecognizer.FlatteningStrategy strategy = (XMLRecognizer.FlatteningStrategy) this.strategyComboBox.getSelectedItem();

        if (rootElement.isEmpty()) rootElement = null;
        if (recordElement.isEmpty()) recordElement = null;

        return new XMLInfo(rootElement, recordElement, strategy, this.path, this.tableName.toString(), this.columns);
    }
}
