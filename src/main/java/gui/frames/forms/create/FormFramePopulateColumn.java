package gui.frames.forms.create;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;

import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.swing.FontIcon;

public class FormFramePopulateColumn extends FormBase implements ActionListener, DocumentListener, IFormCondition {

    private final List<Column> columns;
    private final DefaultTableModel dataTableModel;
    private final JTable dataTable;

    // Componentes da interface
    private JComboBox<Object> columnComboBox;
    private JLabel lblColumnType;
    private JTextField valueField;
    private JSpinner quantitySpinner;
    private JButton btnAddValue;
    private JButton btnRemoveValue;
    private JTable valuesTable;
    private DefaultTableModel valuesTableModel;
    private JCheckBox preserveExistingCheckbox;

    // Dados dos valores e quantidades
    private final List<ValueQuantityPair> valueQuantityPairs;

    public FormFramePopulateColumn(List<Column> columns, DefaultTableModel dataTableModel, JTable dataTable) {
        super((Window) null);

        this.setModal(true);
        this.setTitle("Populate Column");
        this.columns = new ArrayList<>(columns);
        this.dataTableModel = dataTableModel;
        this.dataTable = dataTable;
        this.valueQuantityPairs = new ArrayList<>();

        this.initGUI();
    }

    private void initGUI() {
        this.setSize(500, 450);

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior - Seleção da coluna
        JPanel topPanel = createColumnSelectionPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Painel central - Configuração de valores
        JPanel centerPanel = createValuesConfigurationPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Painel inferior - Botões
        JPanel bottomPanel = createButtonPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.updateColumnType();
        this.updateButtons();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JPanel createColumnSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Seleção de coluna
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Column:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        this.columnComboBox = new JComboBox<>();
        this.columns.forEach(column -> this.columnComboBox.addItem(column.NAME));
        this.columnComboBox.addActionListener(this);
        panel.add(this.columnComboBox, gbc);

        gbc.gridx = 2;
        this.lblColumnType = new JLabel();
        panel.add(this.lblColumnType, gbc);

        // Checkbox para preservar valores existentes
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        this.preserveExistingCheckbox = new JCheckBox("Preserve existing values");
        this.preserveExistingCheckbox.setToolTipText("If checked, will add new rows instead of overwriting existing values");
        panel.add(this.preserveExistingCheckbox, gbc);

        return panel;
    }

    private JPanel createValuesConfigurationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Painel de entrada de valores
        JPanel inputPanel = createValueInputPanel();
        panel.add(inputPanel, BorderLayout.NORTH);

        // Tabela de valores e quantidades
        JPanel tablePanel = createValuesTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createValueInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de valor
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Value:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        this.valueField = new JTextField(15);
        this.valueField.getDocument().addDocumentListener(this);
        panel.add(this.valueField, gbc);

        // Campo de quantidade
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 3;
        this.quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        this.quantitySpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(this.quantitySpinner, gbc);

        // Botões
        gbc.gridx = 4;
        this.btnAddValue = new JButton("Add");
        this.btnAddValue.setIcon(FontIcon.of(MaterialDesignP.PLUS));
        this.btnAddValue.addActionListener(this);
        panel.add(this.btnAddValue, gbc);

        gbc.gridx = 5;
        this.btnRemoveValue = new JButton("Remove");
        this.btnRemoveValue.setIcon(FontIcon.of(MaterialDesignM.MINUS));
        this.btnRemoveValue.addActionListener(this);
        panel.add(this.btnRemoveValue, gbc);

        return panel;
    }

    private JPanel createValuesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Modelo da tabela
        this.valuesTableModel = new DefaultTableModel(new String[]{"Value", "Quantity", "Percentage"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 2; // Percentual não é editável
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 1 -> Integer.class;
                    case 2 -> String.class;
                    default -> String.class;
                };
            }
        };

        this.valuesTable = new JTable(this.valuesTableModel);
        this.valuesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        this.valuesTable.getSelectionModel().addListSelectionListener(e -> updateButtons());

        // Configurar larguras das colunas
        this.valuesTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        this.valuesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        this.valuesTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(this.valuesTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        this.btnCancel.addActionListener(this);
        this.btnReady.addActionListener(this);

        panel.add(this.btnCancel);
        panel.add(this.btnReady);

        return panel;
    }

    private void updateColumnType() {
        if (this.columnComboBox.getSelectedItem() != null) {
            int columnIndex = this.dataTable.getColumnModel()
                .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());
            ColumnDataType selectedColumnType = this.columns.get(columnIndex).DATA_TYPE;
            this.lblColumnType.setText(String.format("Type: %s", selectedColumnType.toString()));
        }
    }

    private void addValue() {
        String value = this.valueField.getText().trim();
        int quantity = (Integer) this.quantitySpinner.getValue();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar o tipo do valor
        if (!isValidValue(value)) {
            return;
        }

        // Verificar se o valor já existe
        for (ValueQuantityPair pair : this.valueQuantityPairs) {
            if (pair.getValue().equals(value)) {
                JOptionPane.showMessageDialog(this, "Value already exists. Please enter a different value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Adicionar o valor
        ValueQuantityPair newPair = new ValueQuantityPair(value, quantity);
        this.valueQuantityPairs.add(newPair);

        // Atualizar a tabela
        updateValuesTable();

        // Limpar campos
        this.valueField.setText("");
        this.quantitySpinner.setValue(1);

        updateButtons();
    }

    private void removeValue() {
        int selectedRow = this.valuesTable.getSelectedRow();
        if (selectedRow >= 0) {
            this.valueQuantityPairs.remove(selectedRow);
            updateValuesTable();
            updateButtons();
        }
    }

    private boolean isValidValue(String value) {
        int columnIndex = this.dataTable.getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());
        ColumnDataType columnType = this.columns.get(columnIndex).DATA_TYPE;

        try {
            switch (columnType) {
                case INTEGER:
                    Integer.parseInt(value);
                    break;
                case LONG:
                    Long.parseLong(value);
                    break;
                case FLOAT:
                    Float.parseFloat(value);
                    break;
                case DOUBLE:
                    Double.parseDouble(value);
                    break;
                case BOOLEAN:
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Invalid boolean value");
                    }
                    break;
                case CHARACTER:
                    if (value.length() != 1) {
                        throw new IllegalArgumentException("Character must be exactly one character");
                    }
                    break;
                case STRING:
                case NONE:
                    // Qualquer string é válida
                    break;
            }
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                String.format("Invalid value for type %s: %s", columnType.toString(), e.getMessage()),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void updateValuesTable() {
        // Limpar tabela
        this.valuesTableModel.setRowCount(0);

        // Calcular total de quantidades
        int totalQuantity = this.valueQuantityPairs.stream().mapToInt(ValueQuantityPair::getQuantity).sum();

        // Adicionar linhas
        for (ValueQuantityPair pair : this.valueQuantityPairs) {
            double percentage = totalQuantity > 0 ? (double) pair.getQuantity() / totalQuantity * 100.0 : 0.0;
            this.valuesTableModel.addRow(new Object[]{
                pair.getValue(),
                pair.getQuantity(),
                String.format("%.1f%%", percentage)
            });
        }
    }

    private void populateColumn() {
        if (this.valueQuantityPairs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int columnIndex = this.dataTable.getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());

        // Calcular o total de linhas necessárias
        int totalNewRows = this.valueQuantityPairs.stream().mapToInt(ValueQuantityPair::getQuantity).sum();
        int currentRowCount = dataTableModel.getRowCount();
        int finalRowCount = preserveExistingCheckbox.isSelected() ?
            currentRowCount + totalNewRows :
            Math.max(totalNewRows, currentRowCount);

        // Ajustar número de linhas conforme necessário
        while (dataTableModel.getRowCount() < finalRowCount) {
            dataTableModel.addRow(new Object[dataTableModel.getColumnCount()]);
        }

        // Criar lista distribuída de valores
        List<Object> distributedValues = new ArrayList<>();
        for (ValueQuantityPair pair : this.valueQuantityPairs) {
            Object convertedValue = convertValueToColumnType(pair.getValue());
            for (int i = 0; i < pair.getQuantity(); i++) {
                distributedValues.add(convertedValue);
            }
        }

        // Embaralhar para distribuição aleatória
        Collections.shuffle(distributedValues, new Random());

        // Preencher a coluna
        if (preserveExistingCheckbox.isSelected()) {
            // Modo preservar: adicionar novos valores após os existentes
            int startRow = currentRowCount;
            for (int i = 0; i < distributedValues.size(); i++) {
                this.dataTableModel.setValueAt(distributedValues.get(i), startRow + i, columnIndex);
            }
        } else {
            // Modo sobrepor: substituir todos os valores existentes
            for (int i = 0; i < finalRowCount; i++) {
                int valueIndex = i % distributedValues.size(); // Para cobrir todas as linhas
                this.dataTableModel.setValueAt(distributedValues.get(valueIndex), i, columnIndex);
            }
        }

        this.dispose();
    }

    private Object convertValueToColumnType(String value) {
        int columnIndex = this.dataTable.getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());
        ColumnDataType columnType = this.columns.get(columnIndex).DATA_TYPE;

        try {
            return switch (columnType) {
                case INTEGER -> Integer.parseInt(value);
                case LONG -> Long.parseLong(value);
                case FLOAT -> Float.parseFloat(value);
                case DOUBLE -> Double.parseDouble(value);
                case BOOLEAN -> Boolean.parseBoolean(value);
                case CHARACTER -> value.charAt(0);
                default -> value; // STRING, NONE
            };
        } catch (Exception e) {
            return value; // Fallback para string
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnCancel) {
            this.dispose();
        } else if (event.getSource() == this.btnReady) {
            this.populateColumn();
        } else if (event.getSource() == this.columnComboBox) {
            this.updateColumnType();
            // Limpar valores quando trocar de coluna
            this.valueQuantityPairs.clear();
            this.updateValuesTable();
            this.updateButtons();
        } else if (event.getSource() == this.btnAddValue) {
            this.addValue();
        } else if (event.getSource() == this.btnRemoveValue) {
            this.removeValue();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.updateButtons();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.updateButtons();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.updateButtons();
    }

    private void updateButtons() {
        boolean hasValue = !this.valueField.getText().trim().isEmpty();
        boolean hasValues = !this.valueQuantityPairs.isEmpty();
        boolean hasSelection = this.valuesTable.getSelectedRow() >= 0;

        this.btnAddValue.setEnabled(hasValue);
        this.btnRemoveValue.setEnabled(hasSelection);
        this.btnReady.setEnabled(hasValues);

        this.checkBtnReady();
    }

    @Override
    public void checkBtnReady() {
        boolean hasValues = !this.valueQuantityPairs.isEmpty();
        this.btnReady.setEnabled(hasValues);
        this.updateToolTipText(hasValues);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String tooltip = "";
        if (!conditions[0]) {
            tooltip = "Please add at least one value with quantity.";
        }

        UIManager.put("ToolTip.foreground", Color.RED);
        this.btnReady.setToolTipText(tooltip.isEmpty() ? null : tooltip);
    }

    // Classe auxiliar para armazenar valor e quantidade
    private static class ValueQuantityPair {
        private final String value;
        private final int quantity;

        public ValueQuantityPair(String value, int quantity) {
            this.value = value;
            this.quantity = quantity;
        }

        public String getValue() {
            return value;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
