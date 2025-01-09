package gui.frames.jdbc;

import controllers.ConstantController;
import database.jdbc.*;
import enums.JDBCDriver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

public class ConnectionPanel extends JPanel {

    private JComboBox<JDBCDriver> driverComboBox;

    private JTextField hostTextField;

    private JTextField databaseTextField;

    private JTextField userTextField;

    private JPasswordField passwordField;

    private JTextField connectionURLField;

    private ConnectionListPanel leftPanel;

    public ConnectionPanel() {
        initGUI();
    }

    public void setLeftPanel(ConnectionListPanel leftPanel) {
        this.leftPanel = leftPanel;
    }

    private void initGUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Dimension fieldDimension = new Dimension(200, 25);
        driverComboBox = new JComboBox<>(JDBCDriver.values());
        driverComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hostLabel = new JLabel(ConstantController.getString("connections.frame.field.host"));
        hostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hostTextField = new JTextField();
        hostTextField.setMaximumSize(fieldDimension);
        JLabel databaseLabel = new JLabel(ConstantController.getString("connections.frame.field.database"));
        databaseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        databaseTextField = new JTextField();
        databaseTextField.setMaximumSize(fieldDimension);
        JLabel userLabel = new JLabel(ConstantController.getString("connections.frame.field.user"));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userTextField = new JTextField();
        userTextField.setMaximumSize(fieldDimension);
        JLabel passwordLabel = new JLabel(ConstantController.getString("connections.frame.field.password"));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(fieldDimension);
        JLabel connectionURLLabel = new JLabel(ConstantController.getString("connections.frame.field.connectionURL"));
        connectionURLLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectionURLField = new JTextField();
        connectionURLField.setMaximumSize(fieldDimension);
        connectionURLField.setEditable(false);
        setBorder(new EmptyBorder(20, 10, 20, 10));
        add(driverComboBox);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(hostLabel);
        add(hostTextField);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(databaseLabel);
        add(databaseTextField);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(userLabel);
        add(userTextField);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(passwordLabel);
        add(passwordField);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(connectionURLLabel);
        add(connectionURLField);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton saveButton = new JButton(ConstantController.getString("save"));
        JButton testButton = new JButton(ConstantController.getString("test"));
        JButton deleteButton = new JButton(ConstantController.getString("delete"));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(testButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel);
        JButton tablesButton = new JButton(ConstantController.getString("tables"));
        tablesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(tablesButton);
        saveButton.addActionListener(e -> {
            // TODO: Here we create a new connection file instead of updating to re-generate the
            if (leftPanel.getCurrentConnection() != null) {
                leftPanel.getCurrentConnection().delete();
            }

            Object selectedDriver = driverComboBox.getSelectedItem();
            ConnectionConfig connectionConfig;
            if (selectedDriver == JDBCDriver.ORACLE) {
                connectionConfig = new OracleConnectionConfig(
                    hostTextField.getText(), databaseTextField.getText(), userTextField.getText(), String.valueOf(passwordField.getPassword())
                );
            } else if (selectedDriver == JDBCDriver.POSTGRESQL) {
                connectionConfig = new PostgreSQLConnectionConfig(
                    hostTextField.getText(), databaseTextField.getText(), userTextField.getText(), String.valueOf(passwordField.getPassword())
                );
            } else {
                connectionConfig = new MySQLConnectionConfig(
                    hostTextField.getText(), databaseTextField.getText(), userTextField.getText(), String.valueOf(passwordField.getPassword())
                );
            }

            connectionConfig.save();
            leftPanel.setCurrentConnection(connectionConfig);
            boolean connectionIsValid = connectionConfig.test();
            displayTestResult(connectionIsValid);
            leftPanel.updateConnectionList();
        });
        testButton.addActionListener(e -> {
            ConnectionConfig connectionConfig = leftPanel.getCurrentConnection();
            if (connectionConfig != null) {
                boolean connectionIsValid = connectionConfig.test();
                displayTestResult(connectionIsValid);
            }
        });
        deleteButton.addActionListener(e -> {
            ConnectionConfig connectionConfig = leftPanel.getCurrentConnection();
            if (connectionConfig != null) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    ConstantController.getString("connections.frame.dialog.connection.delete"),
                    ConstantController.getString("connections.frame.dialog.connection.delete.title"),
                    JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    connectionConfig.delete();
                    leftPanel.updateConnectionList();
                }
            }
        });
        tablesButton.addActionListener(e -> {
            ConnectionConfig connectionConfig = leftPanel.getCurrentConnection();
            if (connectionConfig != null) {
                if (connectionConfig.test()) {
                    TableSelectionPanel tableSelectionPanel = new TableSelectionPanel(connectionConfig);
                    JDialog tableSelectionDialog = new JDialog();
                    tableSelectionDialog.add(tableSelectionPanel);
                    tableSelectionDialog.pack();
                    tableSelectionDialog.setLocationRelativeTo(null);
                    tableSelectionDialog.setVisible(true);
                } else {
                    displayTestResult(false);
                }
            }
        });
    }

    public void displayConnectionDetails(ConnectionConfig currentConnection) {
        if (currentConnection != null) {
            hostTextField.setText(currentConnection.host);
            databaseTextField.setText(currentConnection.database);
            userTextField.setText(currentConnection.username);
            passwordField.setText(currentConnection.password);
            connectionURLField.setText(currentConnection.connectionURL);
        } else {
            hostTextField.setText("");
            databaseTextField.setText("");
            userTextField.setText("");
            passwordField.setText("");
        }
    }

    private void displayTestResult(boolean testResult) {
        String dialogTitle = ConstantController.getString("jdbc.connection.test.dialog.title");
        if (testResult) {
            JOptionPane.showMessageDialog(this, ConstantController.getString("jdbc.connection.test.dialog.success"), dialogTitle, JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, ConstantController.getString("jdbc.connection.test.dialog.error"), dialogTitle, JOptionPane.ERROR_MESSAGE);
        }
    }
}

