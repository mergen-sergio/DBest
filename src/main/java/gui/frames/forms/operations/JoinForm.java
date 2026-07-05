package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.forms.IFormCondition;
import gui.theme.Themed;
import gui.theme.Theme;
import gui.components.IconButton;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private Cell rightChild;

    private final JComboBox<String> comboBoxSource2 = new JComboBox<>();
    private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

    private final JButton btnColumnSet1 = new IconButton(ConstantController.getString("operationForm.atomicExpression.insert"), null, IconButton.Variant.DEFAULT);
    private final JButton btnRemove = new IconButton(ConstantController.getString("operationForm.remove"), null, IconButton.Variant.DEFAULT);
    private final JButton btnClear = new IconButton(ConstantController.getString("operationForm.clear"), null, IconButton.Variant.DEFAULT);
    private final JButton btnAutoJoin = new IconButton(ConstantController.getString("operationForm.automaticJoin"), null, IconButton.Variant.DEFAULT);

    private final JPanel checkBoxPanel = new JPanel();
    private final List<JCheckBox> joinCheckBoxes = new ArrayList<>();

    @Override
    public void checkBtnReady() {

        boolean noCriteria = joinCheckBoxes.isEmpty();
        btnReady.setEnabled(!noCriteria);
        updateToolTipText(noCriteria);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String btnReadyToolTipText = "";
        boolean noCriteria = conditions[0];
        if (noCriteria) {
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.firstElement");
        }
        UIManager.put("ToolTip.foreground", Color.RED);
        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
    }

    public JoinForm(mxCell jCell) {
        super(jCell);

        rightChild = null;
        if (CellUtils.getActiveCell(jCell).get() instanceof entities.cells.OperationCell operationCell) {
            this.rightChild = operationCell.getRightParent();
        }

        if (this.rightChild != null) {
            rightChild.getColumns().stream()
                    .map(column -> column.SOURCE).distinct()
                    .forEach(comboBoxSource2::addItem);

            setColumnsComboBox(comboBoxColumn2, comboBoxSource2, rightChild.getColumns());
        }

        for (ActionListener actionListener : comboBoxSource.getActionListeners()) {
            comboBoxSource.removeActionListener(actionListener);
        }
        for (ActionListener actionListener : comboBoxSource2.getActionListeners()) {
            comboBoxSource2.removeActionListener(actionListener);
        }

        comboBoxSource.addActionListener(this);
        comboBoxSource2.addActionListener(this);

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

        btnColumnSet1.addActionListener(this);
        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);
        btnAutoJoin.addActionListener(this);

        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        Themed.background(checkBoxPanel, () -> Theme.BACKGROUND);
        checkBoxPanel.setPreferredSize(new Dimension(300, 300));

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.outerTable") + ": "), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.outerColumn") + ": "), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.innerTable") + ": "), 5, 0, 1, 1);
        addExtraComponent(comboBoxSource2, 6, 0, 1, 1);
        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.innerColumn") + ": "), 5, 1, 1, 1);
        addExtraComponent(comboBoxColumn2, 6, 1, 1, 1);

        addExtraComponent(btnColumnSet1, 7, 0, 1, 1);
        addExtraComponent(btnRemove, 7, 1, 1, 1);
        addExtraComponent(btnClear, 7, 2, 1, 1);
        addExtraComponent(btnAutoJoin, 5, 2, 2, 1);

        addExtraComponent(new JScrollPane(checkBoxPanel), 0, 3, 8, 3);

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
            joinCheckBoxes.add(new JCheckBox(previousArgument));
            refreshCheckBoxPanel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkBtnReady();

        if (e.getSource() == comboBoxSource) {
            if (leftChild != null && leftChild.getColumns().stream().anyMatch(column -> column.SOURCE.
                    equals(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()))) {
                setColumns(comboBoxColumn, comboBoxSource, leftChild.getColumns());
            }
        }
        if (e.getSource() == comboBoxSource2) {
            if (rightChild != null && rightChild.getColumns().stream().anyMatch(column -> column.SOURCE.
                    equals(Objects.requireNonNull(comboBoxSource2.getSelectedItem()).toString()))) {
                setColumns(comboBoxColumn2, comboBoxSource2, rightChild.getColumns());
            }
        }

        if (e.getSource() == btnColumnSet1) {

            String s1 = (String) comboBoxSource.getSelectedItem();
            String s2 = (String) comboBoxSource2.getSelectedItem();
            String c1 = (String) comboBoxColumn.getSelectedItem();
            String c2 = (String) comboBoxColumn2.getSelectedItem();
            if (s1 != null && s2 != null && c1 != null && c2 != null) {
                String join = createJoinCriteria(s1, s2, c1, c2);
                if (joinCheckBoxes.stream().noneMatch(checkBox -> checkBox.getText().equals(join))) {
                    joinCheckBoxes.add(new JCheckBox(join));
                    refreshCheckBoxPanel();
                }
            }
        } else if (e.getSource() == btnRemove) {
            joinCheckBoxes.removeIf(AbstractButton::isSelected);
            refreshCheckBoxPanel();
        } else if (e.getSource() == btnClear) {
            joinCheckBoxes.clear();
            refreshCheckBoxPanel();
        } else if (e.getSource() == btnReady) {
            arguments.addAll(joinCheckBoxes.stream().map(JCheckBox::getText).toList());
            btnReady();
        } else if (e.getSource() == btnAutoJoin) {
            autoJoin();
        } else if (e.getSource() == btnCancel) {
            closeWindow();
        }

        checkBtnReady();
    }

    private void refreshCheckBoxPanel() {
        checkBoxPanel.removeAll();
        for (JCheckBox checkBox : joinCheckBoxes) {
            checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();
        checkBoxPanel.repaint();
    }

    private void autoJoin() {
        if (leftChild == null || rightChild == null) return;

        List<String> joins = new ArrayList<>();
        leftChild.getColumns().forEach(leftColumn -> rightChild.getColumns().forEach(rightColumn -> {
            if (leftColumn.NAME.equalsIgnoreCase(rightColumn.NAME)) {
                joins.add(createJoinCriteria(leftColumn.SOURCE, rightColumn.SOURCE,
                        leftColumn.NAME, rightColumn.NAME));
            }
        }));

        for (String join : joins) {
            if (joinCheckBoxes.stream().noneMatch(checkBox -> checkBox.getText().equals(join))) {
                joinCheckBoxes.add(new JCheckBox(join));
            }
        }
        refreshCheckBoxPanel();
    }

    private String createJoinCriteria(String table1, String table2, String col1, String col2) {
        return table1 + "." + col1 + "=" + table2 + "." + col2;
    }

    protected void closeWindow() {
        dispose();
    }
}
