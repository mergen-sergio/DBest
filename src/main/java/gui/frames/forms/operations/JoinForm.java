package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.forms.IFormCondition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;


public class JoinForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private Cell rightChild;

    private final JComboBox<String> comboBoxSource2 = new JComboBox<>();
    private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

    private final JButton btnColumnSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnClear = new JButton(ConstantController.getString("operationForm.remove"));

    private final JTextArea textArea = new JTextArea();

    @Override
    public void checkBtnReady() {

        boolean isTxtField1Empty = textArea.getText().isEmpty() || textArea.getText().isBlank();

        //btnReady.setEnabled(!isTxtField1Empty);

        updateToolTipText(isTxtField1Empty);

    }

    @Override
    public void updateToolTipText(boolean... conditions) {

        String btnReadyToolTipText = "";

        boolean isTxtField1Empty = conditions[0];

        if (isTxtField1Empty) {
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.firstElement");
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    private enum ValueType {
        COLUMN, NUMBER, STRING, NULL, NONE
    }

    public JoinForm(mxCell jCell) {

        super(jCell);

//		parent1.getColumns().stream()
//				.map(column -> column.SOURCE).distinct()
//				.forEach(comboBoxSource2::addItem);
//
//		setColumns(comboBoxColumn2, comboBoxSource2, parent1);
        rightChild = null;

        if (CellUtils.getActiveCell(jCell).get().getParents().size() == 2) {
            this.rightChild = CellUtils.getActiveCell(jCell).get().getParents().get(1);
//			parent2.getColumns().stream()
//					.map(column -> column.SOURCE).distinct()
//					.forEach(comboBoxSource::addItem);

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
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        btnColumnSet1.addActionListener(this);
        btnClear.addActionListener(this);

        //txtFieldValue1.setEditable(false);
        //txtFieldValue2.setEditable(false);
        textArea.setPreferredSize(new Dimension(300, 300));
        textArea.setEditable(false);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.outerTable") + ": "), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.outerColumn") + ": "), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.innerTable") + ": "), 5, 0, 1, 1);
        addExtraComponent(comboBoxSource2, 6, 0, 1, 1);
        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.join.innerColumn") + ": "), 5, 1, 1, 1);
        addExtraComponent(comboBoxColumn2, 6, 1, 1, 1);

        addExtraComponent(btnColumnSet1, 7, 0, 1, 1);
        addExtraComponent(btnClear, 7, 1, 1, 1);

        addExtraComponent(new JScrollPane(textArea), 0, 2, 8, 3);

        setPreviousArgs();

        checkBtnReady();

        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(previousArguments.isEmpty()) return;
        
        String text = String.join("\n", previousArguments);
        textArea.setText(text);
//
//        try {
//
//            System.out.println((previousArguments.get(0)));
//            this.atomicExpression =
//                (AtomicExpression) new BooleanExpressionRecognizer(jCell).recognizer(previousArguments.get(0));
//
//            txtFieldValue1.setText(getText(atomicExpression.getFirstElement()));
//            txtFieldValue2.setText(getText(atomicExpression.getSecondElement()));
//
//            comboBoxOperator.setSelectedIndex(Arrays.stream(RelationalOperator
//                .values()).toList().indexOf(atomicExpression.getRelationalOperator()));
//
//        } catch (BooleanExpressionException e) {
//            new ErrorFrame(e.getMessage());
//        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        checkBtnReady();

        if (e.getSource() == comboBoxSource) {
            if (leftChild.getColumns().stream().anyMatch(column -> column.SOURCE.
                    equals(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()))) {

                setColumns(comboBoxColumn, comboBoxSource, leftChild);

            }
        }
        if (e.getSource() == comboBoxSource2) {
            if (rightChild != null && rightChild.getColumns().stream().anyMatch(column -> column.SOURCE.
                    equals(Objects.requireNonNull(comboBoxSource2.getSelectedItem()).toString()))) {

                setColumns(comboBoxColumn2, comboBoxSource2, rightChild);

            }
        }

        if (e.getSource() == btnColumnSet1) {
            String col1 = comboBoxSource.getSelectedItem() + "." + comboBoxColumn.getSelectedItem();
            String col2 = comboBoxSource2.getSelectedItem() + "." + comboBoxColumn2.getSelectedItem();
            String join = col1 + "=" + col2;
            String previousValue = textArea.getText();
            if (!previousValue.contains(join)) {
                if (previousValue.isBlank()) {
                    textArea.setText(join);
                } else {
                    textArea.setText(previousValue + "\n" + join);
                }
            }

        } else if (e.getSource() == btnClear) {
            textArea.setText("");

        } else if (e.getSource() == btnReady) {
            arguments.addAll(java.util.List.of(textArea.getText().split("\n")));
            //arguments.remove(0);
            btnReady();

        } else if (e.getSource() == btnCancel) {
            closeWindow();
        }

        checkBtnReady();

    }

    protected void closeWindow() {
        dispose();
    }
}
