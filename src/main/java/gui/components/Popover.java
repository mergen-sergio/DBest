package gui.components;

import gui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent.*;

public final class Popover {

    private static final int PADDING_PIXELS = Theme.SPACING_SMALL;
    private static final int ANCHOR_GAP_PIXELS = Theme.SPACING_SMALL;

    private static Popover currentlyVisible = null;

    private final JWindow window;
    private final AWTEventListener outsideClickListener;

    private Popover(Window owner, JComponent content) {
        this.window = new JWindow(owner);
        this.window.setFocusableWindowState(true);
        this.window.setContentPane(buildShell(content));
        this.window.pack();

        this.outsideClickListener = event -> {
            if (event instanceof MouseEvent mouseEvent
                && mouseEvent.getID() == MouseEvent.MOUSE_PRESSED
                && !window.getBounds().contains(mouseEvent.getLocationOnScreen())) {
                hide();
            }
        };

        installEscapeKeyToClose(content);
    }

    public static void showAbove(JComponent anchor, JComponent content) {
        Window owner = SwingUtilities.getWindowAncestor(anchor);
        if (owner == null) {
            return;
        }
        hide();
        Popover popover = new Popover(owner, content);
        popover.positionAbove(anchor);
        popover.window.setVisible(true);
        Toolkit.getDefaultToolkit().addAWTEventListener(
            popover.outsideClickListener, AWTEvent.MOUSE_EVENT_MASK);
        currentlyVisible = popover;
    }

    public static void hide() {
        if (currentlyVisible == null) {
            return;
        }
        Toolkit.getDefaultToolkit().removeAWTEventListener(currentlyVisible.outsideClickListener);
        currentlyVisible.window.setVisible(false);
        currentlyVisible.window.dispose();
        currentlyVisible = null;
    }

    private void positionAbove(JComponent anchor) {
        Point anchorLocation = anchor.getLocationOnScreen();
        int anchorWidth = anchor.getWidth();
        int popoverWidth = window.getWidth();
        int popoverHeight = window.getHeight();

        int centeredX = anchorLocation.x + (anchorWidth - popoverWidth) / 2;
        int aboveY = anchorLocation.y - popoverHeight - ANCHOR_GAP_PIXELS;

        if (aboveY < 0) {
            aboveY = anchorLocation.y + anchor.getHeight() + ANCHOR_GAP_PIXELS;
        }

        window.setLocation(centeredX, aboveY);
    }

    private JPanel buildShell(JComponent content) {
        JPanel shell = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                try {
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setColor(Theme.SURFACE);
                    graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(),
                                             Theme.BORDER_RADIUS_LARGE, Theme.BORDER_RADIUS_LARGE);
                    graphics2D.setColor(Theme.BORDER);
                    graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                                             Theme.BORDER_RADIUS_LARGE, Theme.BORDER_RADIUS_LARGE);
                } finally {
                    graphics2D.dispose();
                }
                super.paintComponent(graphics);
            }
        };
        shell.setOpaque(false);
        shell.setBorder(BorderFactory.createEmptyBorder(
            PADDING_PIXELS, PADDING_PIXELS, PADDING_PIXELS, PADDING_PIXELS));
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    private void installEscapeKeyToClose(JComponent content) {
        KeyStroke escapeStroke = KeyStroke.getKeyStroke("ESCAPE");
        content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
               .put(escapeStroke, "popover.close");
        content.getActionMap().put("popover.close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                hide();
            }
        });
    }
}
