package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import operations.unary.Aggregation;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GroupForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnClear = new JButton(ConstantController.getString("operationForm.clear"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.remove"));
    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));

    private final JPanel checkBoxPanel = new JPanel();
    private final List<JCheckBox> aggregationCheckBoxes = new ArrayList<>();

    private final JComboBox<String> comboBoxGroupBySource = new JComboBox<>();
    private final JComboBox<String> comboBoxGroupByColumn = new JComboBox<>();
    private final JComboBox<String> comboBoxAggregation = new JComboBox<>(
            Arrays.stream(Aggregation.Function.values())
                    .map(Aggregation.Function::getDisplayName)
                    .toArray(String[]::new));

    public GroupForm(mxCell jCell) {
        super(jCell);
        initGUI();
    }

    public void initGUI() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.setPreferredSize(new Dimension(300, 300));

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source") + ":"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.column") + ":"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operation.aggregation") + ":"), 0, 2, 1, 1);
        addExtraComponent(comboBoxAggregation, 1, 2, 2, 1);

        addExtraComponent(btnAdd, 0, 3, 1, 1);
        addExtraComponent(btnRemove, 1, 3, 1, 1);
        addExtraComponent(btnClear, 2, 3, 1, 1);

        addExtraComponent(new JScrollPane(checkBoxPanel), 0, 4, 3, 2);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source") + ":"), 0, 6, 1, 1);
        addExtraComponent(comboBoxGroupBySource, 1, 6, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.groupBy") + ":"), 0, 7, 1, 1);
        addExtraComponent(comboBoxGroupByColumn, 1, 7, 2, 1);

        comboBoxGroupBySource.addActionListener(actionEvent ->
                setColumns(comboBoxGroupByColumn, comboBoxGroupBySource, leftChild.getColumns()));

        Cell cell = CellUtils.getActiveCell(jCell).get();
        List<Column> columns = setLeftComboBoxColumns(cell);
        setComboBoxData(columns, comboBoxGroupBySource, comboBoxGroupByColumn);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (previousArguments.isEmpty()) return;

        String groupByColumn = previousArguments.get(0);
        String groupByColumnName = Column.removeSource(groupByColumn);
        String groupByColumnSource = Column.removeName(groupByColumn);

        comboBoxGroupBySource.setSelectedItem(groupByColumnSource);
        comboBoxGroupByColumn.setSelectedItem(groupByColumnName);

        for (String element : previousArguments.subList(1, previousArguments.size())) {
            aggregationCheckBoxes.add(new JCheckBox(element));
        }
        refreshCheckBoxPanel();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnAdd) {
            if (comboBoxColumn.getItemCount() > 0) {
                String aggregation = createAggregationString();
                if (aggregation != null
                        && aggregationCheckBoxes.stream().noneMatch(cb -> cb.getText().equals(aggregation))) {
                    aggregationCheckBoxes.add(new JCheckBox(aggregation));
                    refreshCheckBoxPanel();
                }
            }
        } else if (actionEvent.getSource() == btnRemove) {
            aggregationCheckBoxes.removeIf(AbstractButton::isSelected);
            refreshCheckBoxPanel();
        } else if (actionEvent.getSource() == btnClear) {
            aggregationCheckBoxes.clear();
            refreshCheckBoxPanel();
        } else if (actionEvent.getSource() == btnCancel) {
            closeWindow();
        } else if (actionEvent.getSource() == btnReady) {
            Object gbSource = comboBoxGroupBySource.getSelectedItem();
            Object gbColumn = comboBoxGroupByColumn.getSelectedItem();
            if (gbSource == null || gbColumn == null) return;
            arguments.clear();
            arguments.add(Column.composeSourceAndName(
                    Objects.requireNonNull(gbSource).toString(),
                    Objects.requireNonNull(gbColumn).toString()));
            arguments.addAll(aggregationCheckBoxes.stream().map(JCheckBox::getText).toList());
            btnReady();
        }
    }

    private String createAggregationString() {
        Object selectedFn = comboBoxAggregation.getSelectedItem();
        Object selectedSource = comboBoxSource.getSelectedItem();
        Object selectedColumn = comboBoxColumn.getSelectedItem();
        if (selectedFn == null || selectedSource == null || selectedColumn == null) return null;

        String selectedName = selectedFn.toString();
        String prefix = null;
        for (Aggregation.Function fn : Aggregation.Function.values()) {
            if (fn.getDisplayName().equals(selectedName)) {
                prefix = fn.getPrefix();
                break;
            }
        }
        if (prefix == null) return null;

        return prefix + selectedSource + "." + selectedColumn;
    }

    private void refreshCheckBoxPanel() {
        checkBoxPanel.removeAll();
        for (JCheckBox checkBox : aggregationCheckBoxes) {
            checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();
        checkBoxPanel.repaint();
    }

    protected void closeWindow() {
        dispose();
    }
}
