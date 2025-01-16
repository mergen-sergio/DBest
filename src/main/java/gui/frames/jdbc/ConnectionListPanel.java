package gui.frames.jdbc;

import controllers.ConstantController;
import database.jdbc.ConnectionConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ConnectionListPanel extends JPanel {
    private static ConnectionConfig currentConnection;
    private JList<String> connectionList;
    private DefaultListModel<String> connectionListModel;
    private ConnectionPanel rightPanel;
    private TablesPanel tablesPanel;
    private final JLabel titleLabel;

    public ConnectionListPanel() {
        titleLabel = new JLabel("Connection List");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        initGui();
    }

    public void setRightPanel(ConnectionPanel rightPanel) {
        this.rightPanel = rightPanel;
    }

    public void setTablesPanel(TablesPanel tablesPanel) {
        this.tablesPanel = tablesPanel;
    }

    private void initGui() {
        setLayout(new BorderLayout());
        connectionListModel = new DefaultListModel<>();
        connectionList = new JList<>(connectionListModel);
        updateConnectionList();
        setBorder(new EmptyBorder(20, 10, 20, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JScrollPane configuredConnectionsList = new JScrollPane(connectionList);
        add(configuredConnectionsList, BorderLayout.CENTER);

        JButton addButton = new JButton(ConstantController.getString("connections.frame.button.new"));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            rightPanel.displayConnectionDetails(null);
            connectionList.clearSelection();
            updateConnectionList();
        });

        connectionList.addListSelectionListener(e -> {
            tablesPanel.setVisible(true);
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = connectionList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentConnection = ConnectionConfig.getAllConfiguredConnections().get(selectedIndex);
                    rightPanel.displayConnectionDetails(currentConnection);
                    // Test connection and load tables automatically
                    if (currentConnection.test()) {
                        tablesPanel.updateTables(currentConnection);
                    } else {
                        tablesPanel.clear();
                    }
                }
            }
        });
    }

    public static ConnectionConfig getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(ConnectionConfig connection) {
        currentConnection = connection;
        tablesPanel.setVisible(true);
        if (tablesPanel != null) {
            if (connection != null && connection.test()) {
                tablesPanel.updateTables(connection);
            } else {
                tablesPanel.clear();
            }
        }
    }

    public void updateConnectionList() {
        connectionListModel.clear();
        ArrayList<ConnectionConfig> configuredConnections = ConnectionConfig.getAllConfiguredConnections();
        for (ConnectionConfig connection : configuredConnections) {
            connectionListModel.addElement(connection.host);
        }
        connectionList.revalidate();
        if (tablesPanel != null) {
            tablesPanel.updateTables(null);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Dynamically calculate width as 1/6 of the parent frame's width
        int width = getParent() != null ? getParent().getWidth() / 6 : 300;
        return new Dimension(width, super.getPreferredSize().height);
    }
}
