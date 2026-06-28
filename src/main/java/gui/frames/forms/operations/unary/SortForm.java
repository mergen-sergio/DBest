package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import operations.unary.Sort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SortForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final JRadioButton ascendingRadioButton = new JRadioButton(ConstantController.getString("operationForm.ascending"));
    private final JRadioButton descendingRadioButton = new JRadioButton(ConstantController.getString("operationForm.descending"));

    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.remove"));
    private final JButton btnClear = new JButton(ConstantController.getString("operationForm.clear"));
    private final JButton btnUp = new JButton(ConstantController.getString("operationForm.up"));
    private final JButton btnDown = new JButton(ConstantController.getString("operationForm.down"));

    private final DefaultListModel<String> sortCriteriaModel = new DefaultListModel<>();
    private final JList<String> sortCriteriaList = new JList<>(sortCriteriaModel);

    public SortForm(mxCell jCell) {

        super(jCell);

        initGUI();

    }

    public void initGUI() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        ascendingRadioButton.setSelected(true);

        buttonGroup.add(ascendingRadioButton);
        buttonGroup.add(descendingRadioButton);

        ascendingRadioButton.addActionListener(this);
        descendingRadioButton.addActionListener(this);
        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);
        btnUp.addActionListener(this);
        btnDown.addActionListener(this);
        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        sortCriteriaList.setVisibleRowCount(8);
        sortCriteriaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sortCriteriaList.setPreferredSize(new Dimension(320, 160));

        addExtraComponent(ascendingRadioButton, 0, 2, 1, 1);
        addExtraComponent(descendingRadioButton, 1, 2, 1, 1);
        addExtraComponent(btnAdd, 0, 3, 1, 1);
        addExtraComponent(btnRemove, 1, 3, 1, 1);
        addExtraComponent(btnClear, 2, 3, 1, 1);
        addExtraComponent(btnUp, 3, 3, 1, 1);
        addExtraComponent(btnDown, 4, 3, 1, 1);
        addExtraComponent(new JScrollPane(sortCriteriaList), 0, 4, 5, 3);

        setPreviousArgs();
        checkBtnReady();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if (previousArguments.isEmpty()) return;

        for (String previousArgument : previousArguments) {
            String column = Sort.removeOrderPrefix(previousArgument);
            String criterion = Sort.getPrefix(Sort.isAscending(previousArgument)) + column;

            if (!sortCriteriaModel.contains(criterion)) {
                sortCriteriaModel.addElement(criterion);
                restrictedColumns.add(column);
            }
        }

        String firstCriterion = sortCriteriaModel.getElementAt(0);
        String firstColumn = Sort.removeOrderPrefix(firstCriterion);
        comboBoxSource.setSelectedItem(Column.removeName(firstColumn));
        comboBoxColumn.setSelectedItem(Column.removeSource(firstColumn));
        descendingRadioButton.setSelected(!Sort.isAscending(firstCriterion));
        ascendingRadioButton.setSelected(Sort.isAscending(firstCriterion));

        refreshAvailableColumns();

    }

    @Override
    public void checkBtnReady() {

        boolean hasCriteria = !sortCriteriaModel.isEmpty();

        btnReady.setEnabled(hasCriteria);

        updateToolTipText(hasCriteria);

    }

    @Override
    public void updateToolTipText(boolean ...conditions) {

        String btnReadyToolTipText = "";

        boolean hasCriteria = conditions[0];

        if (!hasCriteria)
            btnReadyToolTipText = "- "+ ConstantController.getString("operationForm.toolTip.sort.selectAtLeastOne");

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == btnAdd) {

            addCriterion();

        } else if (actionEvent.getSource() == btnRemove) {

            removeSelectedCriteria();

        } else if (actionEvent.getSource() == btnClear) {

            clearCriteria();

        } else if (actionEvent.getSource() == btnUp) {

            moveSelectedCriterion(-1);

        } else if (actionEvent.getSource() == btnDown) {

            moveSelectedCriterion(1);

        } else if (actionEvent.getSource() == btnReady) {

            arguments.clear();
            for (int i = 0; i < sortCriteriaModel.size(); i++) {
                arguments.add(sortCriteriaModel.getElementAt(i));
            }
            btnReady();

        } else if (actionEvent.getSource() == btnCancel) {

            closeWindow();

        }

        checkBtnReady();
    }

    private void addCriterion() {
        if (comboBoxSource.getSelectedItem() == null || comboBoxColumn.getSelectedItem() == null) {
            return;
        }

        String sourceAndName = Column.composeSourceAndName(
            comboBoxSource.getSelectedItem().toString(),
            comboBoxColumn.getSelectedItem().toString()
        );

        if (restrictedColumns.contains(sourceAndName)) {
            return;
        }

        String criterion = Sort.getPrefix(ascendingRadioButton.isSelected()) + sourceAndName;
        sortCriteriaModel.addElement(criterion);
        restrictedColumns.add(sourceAndName);
        refreshAvailableColumns();
    }

    private void removeSelectedCriteria() {
        for (String criterion : sortCriteriaList.getSelectedValuesList()) {
            sortCriteriaModel.removeElement(criterion);
            restrictedColumns.remove(Sort.removeOrderPrefix(criterion));
        }

        refreshAvailableColumns();
    }

    private void clearCriteria() {
        sortCriteriaModel.clear();
        restrictedColumns.clear();
        refreshAvailableColumns();
    }

    private void moveSelectedCriterion(int direction) {
        int selectedIndex = sortCriteriaList.getSelectedIndex();
        int newIndex = selectedIndex + direction;

        if (selectedIndex < 0 || newIndex < 0 || newIndex >= sortCriteriaModel.size()) {
            return;
        }

        String criterion = sortCriteriaModel.remove(selectedIndex);
        sortCriteriaModel.add(newIndex, criterion);
        sortCriteriaList.setSelectedIndex(newIndex);
    }

    private void refreshAvailableColumns() {
        if (leftChild == null) {
            return;
        }

        setColumns(comboBoxColumn, comboBoxSource, leftChild.getColumns());
    }

    protected void closeWindow() {
        dispose();
    }
}
