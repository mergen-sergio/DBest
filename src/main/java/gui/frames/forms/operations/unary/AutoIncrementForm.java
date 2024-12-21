package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

public class AutoIncrementForm extends OperationForm implements IOperationForm, ActionListener {

    private final JTextField txtFieldCol = new JTextField();

    public AutoIncrementForm(mxCell jCell) {

        super(jCell);

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

        txtFieldCol.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }
        });

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.tuplesToRead")), 0, 0, 1, 1);
        addExtraComponent(txtFieldCol, 1, 0, 2, 1);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void verifyConditions() {
        btnReady.setEnabled(
                !txtFieldCol.getText().isBlank() && !txtFieldCol.getText().isEmpty()
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnCancel) {

            closeWindow();

        } else if (e.getSource() == btnReady) {

            arguments.add(txtFieldCol.getText());
            btnReady();

        }

        verifyConditions();

    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    protected void setPreviousArgs() {
        if (!previousArguments.isEmpty()) {
            txtFieldCol.setText(previousArguments.get(0));
        }
    }

}
