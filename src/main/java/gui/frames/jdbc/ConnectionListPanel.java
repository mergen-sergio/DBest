package gui.frames.jdbc;

import controllers.ConstantController;
import database.jdbc.ConnectionConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.ArrayList;

public class ConnectionListPanel extends JPanel {

    private ConnectionConfig currentConnection;

    private JList<String> connectionList;

    private DefaultListModel<String> connectionListModel;

    private ConnectionPanel rightPanel;

    public ConnectionListPanel() {
        initGui();
    }

    public void setRightPanel(ConnectionPanel rightPanel) {
        this.rightPanel = rightPanel;
    }

    private void initGui() {
        setLayout(new GridLayout(2, 1));
        connectionListModel = new DefaultListModel<>();
        connectionList = new JList<>(connectionListModel);
        updateConnectionList();
        setBorder(new EmptyBorder(20, 10, 20, 0));
        JScrollPane configuredConnectionsList = new JScrollPane(connectionList);
        add(configuredConnectionsList);
        JButton addButton = new JButton(ConstantController.getString("connections.frame.button.new"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        add(buttonPanel);
        addButton.addActionListener(e -> {
            rightPanel.displayConnectionDetails(null);
            connectionList.clearSelection();
        });
        connectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = connectionList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentConnection = ConnectionConfig.getAllConfiguredConnections().get(selectedIndex);
                    rightPanel.displayConnectionDetails(currentConnection);
                }
            }
        });
    }

    public ConnectionConfig getCurrentConnection() {
        return this.currentConnection;
    }

    public void setCurrentConnection(ConnectionConfig currentConnection) {
        this.currentConnection = currentConnection;
    }

    public void updateConnectionList() {
        connectionListModel.clear();
        ArrayList<ConnectionConfig> configuredConnections = ConnectionConfig.getAllConfiguredConnections();
        for (ConnectionConfig connection : configuredConnections) {
            connectionListModel.addElement(connection.host);
        }
        connectionList.revalidate();
    }
}
