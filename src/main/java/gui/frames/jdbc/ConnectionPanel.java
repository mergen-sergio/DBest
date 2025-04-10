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
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 5, 20, 5));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        fieldsPanel.add(new JLabel("Driver"), gbc);
        gbc.gridy++;
        driverComboBox = new JComboBox<>(JDBCDriver.values());
        fieldsPanel.add(driverComboBox, gbc);

        gbc.gridy++;
        fieldsPanel.add(new JLabel(ConstantController.getString("connections.frame.field.host")), gbc);
        gbc.gridy++;
        hostTextField = new JTextField(20);
        fieldsPanel.add(hostTextField, gbc);

        gbc.gridy++;
        fieldsPanel.add(new JLabel(ConstantController.getString("connections.frame.field.database")), gbc);
        gbc.gridy++;
        databaseTextField = new JTextField(20);
        fieldsPanel.add(databaseTextField, gbc);

        gbc.gridy++;
        fieldsPanel.add(new JLabel(ConstantController.getString("connections.frame.field.user")), gbc);
        gbc.gridy++;
        userTextField = new JTextField(20);
        fieldsPanel.add(userTextField, gbc);

        gbc.gridy++;
        fieldsPanel.add(new JLabel(ConstantController.getString("connections.frame.field.password")), gbc);
        gbc.gridy++;
        passwordField = new JPasswordField(20);
        fieldsPanel.add(passwordField, gbc);

        gbc.gridy++;
        fieldsPanel.add(new JLabel(ConstantController.getString("connections.frame.field.connectionURL")), gbc);
        gbc.gridy++;
        connectionURLField = new JTextField(20);
        connectionURLField.setEditable(false);
        fieldsPanel.add(connectionURLField, gbc);

        add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton(ConstantController.getString("save"));
        JButton testButton = new JButton(ConstantController.getString("test"));
        JButton deleteButton = new JButton(ConstantController.getString("delete"));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(testButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            // TODO: Here we create a new connection file instead of updating the existing one
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
            leftPanel.updateConnectionList();
            leftPanel.setCurrentConnection(connectionConfig);
            displayConnectionDetails(connectionConfig);
            boolean connectionIsValid = connectionConfig.test();
            displayTestResult(connectionIsValid);
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
                    displayConnectionDetails(null);
                }
            }
        });
    }

    public void displayConnectionDetails(ConnectionConfig currentConnection) {
        if (currentConnection != null) {
            leftPanel.setCurrentConnection(currentConnection);
            driverComboBox.setSelectedItem(currentConnection.getDriver());
            hostTextField.setText(currentConnection.host);
            databaseTextField.setText(currentConnection.database);
            userTextField.setText(currentConnection.username);
            passwordField.setText(currentConnection.password);
            connectionURLField.setText(currentConnection.connectionURL);
        } else {
            leftPanel.setCurrentConnection(null);
            hostTextField.setText("");
            databaseTextField.setText("");
            userTextField.setText("");
            passwordField.setText("");
            connectionURLField.setText("");
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

