package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.theme.Themed;
import gui.theme.Theme;
import gui.components.IconButton;
import gui.frames.forms.operations.OperationForm;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ProjectionForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnAdd = new IconButton(ConstantController.getString("operationForm.add"), null, IconButton.Variant.DEFAULT);
    private final JButton btnRemove = new IconButton(ConstantController.getString("operationForm.remove"), null, IconButton.Variant.DEFAULT);
    private final JButton btnClear = new IconButton(ConstantController.getString("operationForm.clear"), null, IconButton.Variant.DEFAULT);
    private final JButton btnAddAll = new IconButton(ConstantController.getString("operationForm.addAllColumns"), null, IconButton.Variant.DEFAULT);

    private final JPanel checkBoxPanel = new JPanel();
    private final List<JCheckBox> projectionCheckBoxes = new ArrayList<>();

    public ProjectionForm(mxCell jCell) {
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

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        Themed.background(checkBoxPanel, () -> Theme.BACKGROUND);
        checkBoxPanel.setPreferredSize(new Dimension(300, 300));

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);
        btnAddAll.addActionListener(this);

        addExtraComponent(btnAdd, 0, 2, 1, 1);
        addExtraComponent(btnAddAll, 1, 2, 1, 1);
        addExtraComponent(btnRemove, 2, 2, 1, 1);
        addExtraComponent(btnClear, 3, 2, 1, 1);
        addExtraComponent(new JScrollPane(checkBoxPanel), 0, 3, 4, 3);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (previousArguments.isEmpty()) return;
        for (String element : previousArguments) {
            if (projectionCheckBoxes.stream().noneMatch(cb -> cb.getText().equals(element))) {
                projectionCheckBoxes.add(new JCheckBox(element));
                String columnName = Column.removeSource(element);
                comboBoxColumn.removeItem(columnName);
            }
        }
        refreshCheckBoxPanel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            if (comboBoxColumn.getItemCount() > 0) {
                addColumn();
            }
        } else if (e.getSource() == btnRemove) {
            List<JCheckBox> selectedBoxes = projectionCheckBoxes.stream()
                    .filter(AbstractButton::isSelected)
                    .toList();

            for (JCheckBox checkBox : selectedBoxes) {
                String columnName = Column.removeSource(checkBox.getText());
                comboBoxColumn.addItem(columnName);
            }

            projectionCheckBoxes.removeIf(AbstractButton::isSelected);
            refreshCheckBoxPanel();

        } else if (e.getSource() == btnClear) {
            projectionCheckBoxes.clear();
            restrictedColumns.clear();
            comboBoxColumn.removeAllItems();
            leftChild.getColumnNames().forEach(comboBoxColumn::addItem);
            refreshCheckBoxPanel();

        } else if (e.getSource() == btnCancel) {
            closeWindow();
        } else if (e.getSource() == btnReady) {
            arguments.addAll(projectionCheckBoxes.stream().map(JCheckBox::getText).toList());
            btnReady();
        } else if (e.getSource() == btnAddAll) {
            while (comboBoxColumn.getItemCount() != 0) {
                comboBoxColumn.setSelectedIndex(0);
                if (!addColumn()) break;
            }
        }
    }


    private boolean addColumn() {
        Object source = comboBoxSource.getSelectedItem();
        Object column = comboBoxColumn.getSelectedItem();
        if (source == null || column == null) return false;

        String composed = source + "." + column;

        projectionCheckBoxes.add(new JCheckBox(composed));
        refreshCheckBoxPanel();

        restrictedColumns.add(composed);
        int idx = comboBoxColumn.getSelectedIndex();
        if (idx >= 0) comboBoxColumn.removeItemAt(idx);
        return true;
    }

    private void refreshCheckBoxPanel() {
        checkBoxPanel.removeAll();
        for (JCheckBox checkBox : projectionCheckBoxes) {
            checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();
        checkBoxPanel.repaint();
    }

    protected void closeWindow() {
        dispose();
    }
}
