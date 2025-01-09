package gui.frames.jdbc;

import controllers.ConstantController;
import controllers.MainController;

import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectionsFrame extends JFrame {

    public ConnectionsFrame() {
        initGUI();
    }

    private void initGUI() {
        setResizable(false);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle(ConstantController.getString("connections"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                MainController.connectionsFrame = null;
            }
        });

        ConnectionPanel rightPanel = new ConnectionPanel();
        ConnectionListPanel leftPanel = new ConnectionListPanel();

        // FIXME: Not the best approach probably
        rightPanel.setLeftPanel(leftPanel);
        leftPanel.setRightPanel(rightPanel);
        leftPanel.setPreferredSize(new Dimension(100, getHeight()));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
        pack();
    }
}
