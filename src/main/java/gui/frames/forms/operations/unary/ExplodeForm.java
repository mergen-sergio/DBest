package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
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

public class ExplodeForm extends OperationForm implements ActionListener, IOperationForm {

    private final JTextField txtFieldDelimiter = new JTextField();

    public ExplodeForm(mxCell jCell) {
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

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.delimiter")), 0, 3, 1, 1);
        addExtraComponent(txtFieldDelimiter, 1, 3, 2, 1);

        Forms.onDocumentChange(txtFieldDelimiter, this::verifyConditions);


        ((AbstractDocument) txtFieldDelimiter.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - 1 - length;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (!text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void verifyConditions() {

        boolean delimiterValid = !Forms.isBlank(txtFieldDelimiter.getText());
        boolean sourceSelected = comboBoxSource.getSelectedItem() != null;
        boolean columnSelected = comboBoxColumn.getSelectedItem() != null;
        btnReady.setEnabled(delimiterValid && sourceSelected && columnSelected);
    }

    @Override
    protected void setPreviousArgs() {
        if (!previousArguments.isEmpty()) {
            String column = previousArguments.get(0);
            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            comboBoxSource.setSelectedItem(columnSource);
            comboBoxColumn.setSelectedItem(columnName);
        }
        if (previousArguments.size() > 1) {
            txtFieldDelimiter.setText(previousArguments.get(1));
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnReady) {

            String source = comboBoxSource.getSelectedItem().toString();
            String column = comboBoxColumn.getSelectedItem().toString();
            arguments.add(Column.composeSourceAndName(source, column));
            arguments.add(txtFieldDelimiter.getText());
            btnReady();
        } else if (actionEvent.getSource() == btnCancel) {
            closeWindow();
        }
    }

    protected void closeWindow() {
        dispose();
    }
}
