package gui.frames.jdbc;

import javax.swing.*;

import controllers.MainController;
import database.TableCreator;
import database.jdbc.*;
import entities.cells.TableCell;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TableSelectionPanel extends JPanel {

    private final JList<String> tableList;

    public TableSelectionPanel(ConnectionConfig connectionConfig) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        tableList = new JList<>(listModel);
        JButton importButton = new JButton("Import");
        setLayout(new BorderLayout());
        ArrayList<String> tables = connectionConfig.getTableNames();
        for (String table : tables) {
            listModel.addElement(table);
        }
        JScrollPane scrollPane = new JScrollPane(tableList);
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(importButton);
        add(buttonPanel, BorderLayout.SOUTH);
        importButton.addActionListener(e -> {
            List<String> selectedTables = tableList.getSelectedValuesList();
            for (String selectedTable : selectedTables) {
                TableCell tableCell = TableCreator.createJDBCTable(selectedTable, connectionConfig, false);
                MainController.saveTable(tableCell);
            }
        });
    }
}
