package gui.frames.jdbc;

import controllers.ConstantController;
import controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectionsFrame extends JFrame {

    public ConnectionsFrame() {
        initGUI();
    }

    private void initGUI() {
        setSize(1000, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle(ConstantController.getString("connections"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                MainController.connectionsFrame = null;
            }
        });

        ConnectionPanel connectionPanel = new ConnectionPanel();
        ConnectionListPanel leftPanel = new ConnectionListPanel();
        TablesPanel tablesPanel = new TablesPanel();

        connectionPanel.setLeftPanel(leftPanel);
        leftPanel.setRightPanel(connectionPanel);
        leftPanel.setTablesPanel(tablesPanel);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(leftPanel);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setLeftComponent(connectionPanel);
        rightSplitPane.setRightComponent(tablesPanel);

        mainSplitPane.setRightComponent(rightSplitPane);

        rightSplitPane.setDividerLocation(350);

        getContentPane().add(mainSplitPane);

        setVisible(true);
        pack();
    }
}
