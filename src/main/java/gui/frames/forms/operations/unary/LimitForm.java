package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import gui.utils.Forms;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LimitForm extends OperationForm implements IOperationForm, ActionListener {

    private final JTextField txtFieldTuplesToRead = new JTextField();
    private final JTextField txtFieldStartingTuple = new JTextField();

    public LimitForm(mxCell jCell) {
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

        Forms.onDocumentChange(txtFieldTuplesToRead, this::verifyConditions);
        Forms.onDocumentChange(txtFieldStartingTuple, this::verifyConditions);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.tuplesToRead")), 0, 0, 1, 1);
        addExtraComponent(txtFieldTuplesToRead, 1, 0, 2, 1);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.startingTuple")), 0, 1, 1, 1);
        addExtraComponent(txtFieldStartingTuple, 1, 1, 2, 1);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void verifyConditions() {
        boolean tuplesValid = Forms.parsePositiveIntOr(txtFieldTuplesToRead.getText(), -1) > 0;
        boolean startingValid = Forms.parsePositiveIntOr(txtFieldStartingTuple.getText(), -1) > 0;
        btnReady.setEnabled(tuplesValid && startingValid);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            closeWindow();
        } else if (e.getSource() == btnReady) {
            arguments.add(txtFieldTuplesToRead.getText());
            arguments.add(txtFieldStartingTuple.getText());
            btnReady();
        }
        verifyConditions();
    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    protected void setPreviousArgs() {
        if (previousArguments.size() == 2) {
            txtFieldTuplesToRead.setText(previousArguments.get(0));
            txtFieldStartingTuple.setText(previousArguments.get(1));
        }
    }
}
