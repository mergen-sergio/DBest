package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;

import entities.Column;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;

import files.ExportFile;

public class ExportAsForm extends ImportExportAsForm implements ActionListener {

    private final AtomicReference<Boolean> cancelService;

    private final JButton btnUniqueIndex = new JButton(ConstantController.getString("exportAs.uniqueIndexButton"));

    private final JButton btnNonUniqueIndex = new JButton(ConstantController.getString("exportAs.nonUniqueIndexButton"));
    
    private final JButton btnSqlScript = new JButton(ConstantController.getString("exportAs.scriptSQLButton"));

    private final Cell cell;

    public ExportAsForm(mxCell jCell, AtomicReference<Boolean> cancelService) {
        this.setModal(true);
        
        this.setTitle(ConstantController.getString("exportAs.title"));

        this.cancelService = cancelService;
        this.cell = CellUtils.getActiveCell(jCell).orElse(null);

        this.initGUI();
    }

    public void initGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                ExportAsForm.this.closeWindow();
            }
        });

        JPanel pane = new JPanel(new FlowLayout());

        pane.add(this.btnUniqueIndex);
        pane.add(this.btnNonUniqueIndex);
        pane.add(this.btnCsv);
        pane.add(this.btnSqlScript);

        this.contentPanel.add(pane, BorderLayout.CENTER);
        this.btnSqlScript.addActionListener(this);
        this.btnUniqueIndex.addActionListener(this);
        this.btnNonUniqueIndex.addActionListener(this);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnCancel) {
            this.cancelService.set(true);
            this.closeWindow();
        } else if (event.getSource() == this.btnCsv) {
            this.closeWindow();

            if (!this.cancelService.get()) {
                new ExportFile().exportToCSV(this.cell);
            }
        } else if (event.getSource() == this.btnUniqueIndex) {
            this.closeWindow();

            List<Column> primaryKeyColumns = new PKChooserForm(this.cell, true).getSelectedColumns();
            if(!this.cancelService.get() && !primaryKeyColumns.isEmpty()) {
                new ExportFile().exportToFYI(this.cell, primaryKeyColumns, true);
            }
        }
            else if (event.getSource() == this.btnNonUniqueIndex) {
            this.closeWindow();

            List<Column> primaryKeyColumns = new PKChooserForm(this.cell, false).getSelectedColumns();
            if(!this.cancelService.get() && !primaryKeyColumns.isEmpty()) {
                new ExportFile().exportToFYI(this.cell, primaryKeyColumns, false);
            }
        } else if (event.getSource() == this.btnSqlScript) {
            this.closeWindow();

            if (!this.cancelService.get()) {
                new ExportFile().exportToMySQLScript(this.cell);
            }
        }
    }

    protected void closeWindow() {
        this.dispose();
    }
}
