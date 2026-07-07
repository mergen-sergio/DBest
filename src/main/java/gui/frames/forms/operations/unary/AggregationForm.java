package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import operations.unary.Aggregation;
import utils.Utils;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class AggregationForm extends OperationForm implements ActionListener, IOperationForm {

    protected final JComboBox<String> comboBoxAggregation = new JComboBox<>(Arrays.stream(Aggregation.Function.values())
            .map(Aggregation.Function::getDisplayName)
            .toArray(String[]::new));

    public AggregationForm(mxCell jCell) {
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

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source") + ":"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.column") + ":"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operation.aggregation") + ":"), 0, 2, 1, 1);
        addExtraComponent(comboBoxAggregation, 1, 2, 1, 1);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Aggregation.Function selectedFunction() {
        Object selected = comboBoxAggregation.getSelectedItem();
        if (selected == null) return null;
        String selectedName = selected.toString();
        for (Aggregation.Function fn : Aggregation.Function.values()) {
            if (fn.getDisplayName().equals(selectedName)) return fn;
        }
        return null;
    }

    @Override
    protected void setPreviousArgs() {
        if (previousArguments.isEmpty()) return;

        String column = previousArguments.get(0);

        if (Utils.startsWithIgnoreCase(column, Aggregation.PREFIXES)) {
            String prefix = Utils.getFirstMatchingPrefixIgnoreCase(column, Aggregation.PREFIXES);
            column = column.substring(prefix.length());
            for (Aggregation.Function fn : Aggregation.Function.values()) {
                if (fn.getPrefix().equalsIgnoreCase(prefix)) {
                    comboBoxAggregation.setSelectedItem(fn.getDisplayName());
                    break;
                }
            }
        }

        String columnName = Column.removeSource(column);
        String columnSource = Column.removeName(column);

        comboBoxSource.setSelectedItem(columnSource);
        comboBoxColumn.setSelectedItem(columnName);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnCancel) {
            closeWindow();
        } else if (actionEvent.getSource() == btnReady) {
            Aggregation.Function fn = selectedFunction();
            if (fn == null) {
                return;
            }
            String prefix = fn.getPrefix();
            arguments.add(prefix + comboBoxSource.getSelectedItem() + "." + comboBoxColumn.getSelectedItem());
            btnReady();
        }
    }

    protected void closeWindow() {
        dispose();
    }
}
