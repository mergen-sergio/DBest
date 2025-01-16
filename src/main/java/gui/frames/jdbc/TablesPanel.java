package gui.frames.jdbc;

import controllers.MainController;
import database.TableCreator;
import database.jdbc.ConnectionConfig;
import entities.cells.TableCell;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TablesPanel extends JPanel {
    private final JList<String> tableList;
    private final DefaultListModel<String> tableListModel;
    private final JButton importButton;
    private final JLabel titleLabel;

    public TablesPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 10, 20, 10));

        tableListModel = new DefaultListModel<>();
        tableList = new JList<>(tableListModel);
        JScrollPane scrollPane = new JScrollPane(tableList);

        titleLabel = new JLabel("Database Tables");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        importButton = new JButton("Import Selected Tables");
        importButton.setEnabled(false);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(importButton);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        importButton.addActionListener(e -> importSelectedTables());
    }

    public void updateTables(ConnectionConfig connection) {
        clear();
        if (connection != null) {
            ArrayList<String> tables = connection.getTableNames();
            if (tables != null && !tables.isEmpty()) {
                for (String table : tables) {
                    tableListModel.addElement(table);
                }
                importButton.setEnabled(true);
                titleLabel.setText("Tables in " + connection.database);
            } else {
                importButton.setEnabled(false);
                titleLabel.setText("No tables found");
            }
        }
    }

    private void importSelectedTables() {
        List<String> selectedTables = tableList.getSelectedValuesList();
        if (!selectedTables.isEmpty() && ConnectionListPanel.getCurrentConnection() != null) {
            for (String selectedTable : selectedTables) {
                TableCell tableCell = TableCreator.createJDBCTable(
                    selectedTable,
                    ConnectionListPanel.getCurrentConnection(),
                    true
                );
                MainController.saveTable(tableCell);
            }
            JOptionPane.showMessageDialog(this,
                "Selected tables have been imported successfully.",
                "Import Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void clear() {
        tableListModel.clear();
        importButton.setEnabled(false);
        titleLabel.setText("Database Tables");
    }
}
