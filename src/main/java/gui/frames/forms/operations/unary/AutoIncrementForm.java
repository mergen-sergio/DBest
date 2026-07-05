package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import gui.utils.Forms;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AutoIncrementForm extends OperationForm implements IOperationForm, ActionListener {

    private final JTextField txtFieldCol = new JTextField();
    private final JTextField txtFieldInc = new JTextField();

    public AutoIncrementForm(mxCell jCell) {
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

        Forms.onDocumentChange(txtFieldCol, this::verifyConditions);
        Forms.onDocumentChange(txtFieldInc, this::verifyConditions);


        ((AbstractDocument) txtFieldInc.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.colName")), 0, 0, 1, 1);
        addExtraComponent(txtFieldCol, 1, 0, 2, 1);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.incValue")), 0, 1, 1, 1);
        addExtraComponent(txtFieldInc, 1, 1, 2, 1);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void verifyConditions() {

        boolean colValid = !Forms.isBlank(txtFieldCol.getText());
        boolean incValid = Forms.parsePositiveIntOr(txtFieldInc.getText(), -1) > 0;
        btnReady.setEnabled(colValid && incValid);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            closeWindow();
        } else if (e.getSource() == btnReady) {
            arguments.add(txtFieldCol.getText());
            arguments.add(txtFieldInc.getText());
            btnReady();
        }
        verifyConditions();
    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    protected void setPreviousArgs() {

        if (previousArguments.size() >= 2) {
            txtFieldCol.setText(previousArguments.get(0));
            txtFieldInc.setText(previousArguments.get(1));
        } else {
            txtFieldCol.setText("id");
            txtFieldInc.setText("1");
        }
    }
}
