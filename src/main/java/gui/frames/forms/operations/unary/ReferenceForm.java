package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ReferenceForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.removeColumns"));
    private final JButton btnAddAll = new JButton(ConstantController.getString("operationForm.addAllColumns"));
    private final JTextArea textArea = new JTextArea();

    public ReferenceForm(mxCell jCell) {
        super(jCell);
        initGUI();
    }

    @Override
    protected List<Column> setLeftComboBoxColumns(Cell cell) {
        return getReferences(cell);
    }

    public void initGUI() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        textArea.setPreferredSize(new Dimension(300, 300));
        textArea.setEditable(false);

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnAddAll.addActionListener(this);

        addExtraComponent(btnAdd, 0, 2, 1, 1);
        addExtraComponent(btnAddAll, 1, 2, 1, 1);
        addExtraComponent(btnRemove, 2, 2, 1, 1);
        addExtraComponent(new JScrollPane(textArea), 0, 3, 3, 3);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (previousArguments.isEmpty()) return;
        for (String element : previousArguments) {
            String columnName = Column.removeSource(element);
            String sourceName = Column.removeName(element);
            comboBoxSource.setSelectedItem(sourceName);
            comboBoxColumn.setSelectedItem(columnName);
            if (comboBoxColumn.getItemCount() > 0) {
                updateColumns();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            if (comboBoxColumn.getItemCount() > 0) {
                updateColumns();
            }
        } else if (e.getSource() == btnRemove) {
            textArea.setText("");
            restrictedColumns.clear();
            comboBoxColumn.removeAllItems();
            leftChild.getColumnNames().forEach(comboBoxColumn::addItem);
        } else if (e.getSource() == btnCancel) {
            closeWindow();
        } else if (e.getSource() == btnReady) {

            for (String token : textArea.getText().split("\n")) {
                if (!token.isEmpty()) arguments.add(token);
            }
            btnReady();
        } else if (e.getSource() == btnAddAll) {

            while (comboBoxColumn.getItemCount() != 0) {
                if (!updateColumns()) break;
            }
        }
    }


    private boolean updateColumns() {
        Object source = comboBoxSource.getSelectedItem();
        Object column = comboBoxColumn.getSelectedItem();
        if (source == null || column == null) return false;

        String composed = source + "." + column;
        String textColumnsPicked = textArea.getText() + "\n" + composed;
        restrictedColumns.add(composed);
        int idx = comboBoxColumn.getSelectedIndex();
        if (idx < 0) return false;
        comboBoxColumn.removeItemAt(idx);
        textArea.setText(textColumnsPicked);
        return true;
    }

    protected void closeWindow() {
        dispose();
    }
}
