package gui.components;

import gui.theme.Theme;
import gui.theme.Typography;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class IconButton extends JButton {

    public enum Variant {
        DEFAULT,
        PRIMARY,
        QUIET
    }

    private static final int ICON_SIZE_PIXELS = 14;
    private static final int HORIZONTAL_PADDING_PIXELS = Theme.SPACING_DEFAULT;
    private static final int VERTICAL_PADDING_PIXELS = Theme.SPACING_SMALL + 2;

    private final Variant variant;
    private boolean hovered = false;

    public IconButton(String labelText, Ikon icon, Variant variant) {
        super(labelText == null ? "" : labelText);
        this.variant = variant;

        if (icon != null) {
            Runnable applyIconTint = () -> setIcon(FontIcon.of(icon,
                    ICON_SIZE_PIXELS, foregroundColorFor(variant)));
            applyIconTint.run();
            gui.theme.Theme.addChangeListener(applyIconTint);
        }

        configureBaseAppearance();
        installHoverListener();
    }

    private void configureBaseAppearance() {
        setFont(Typography.BODY);
        gui.theme.Themed.foreground(this, () -> foregroundColorFor(variant));
        setHorizontalAlignment(SwingConstants.CENTER);
        setIconTextGap(Theme.SPACING_SMALL);
        setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_PADDING_PIXELS,
            HORIZONTAL_PADDING_PIXELS,
            VERTICAL_PADDING_PIXELS,
            HORIZONTAL_PADDING_PIXELS));

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void installHoverListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        try {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int radius = Theme.BORDER_RADIUS_DEFAULT;

            Color background = backgroundColorFor(variant, hovered, getModel().isPressed());
            if (background != null) {
                graphics2D.setColor(background);
                graphics2D.fillRoundRect(0, 0, width, height, radius, radius);
            }

            if (isFocusOwner()) {
                graphics2D.setColor(Theme.ACCENT);
                graphics2D.setStroke(new BasicStroke(Theme.STROKE_FOCUS_RING));
                graphics2D.drawRoundRect(1, 1, width - 3, height - 3, radius, radius);
            }
        } finally {
            graphics2D.dispose();
        }

        super.paintComponent(graphics);
    }

    private static Color foregroundColorFor(Variant variant) {
        return switch (variant) {
            case DEFAULT -> Theme.TEXT_PRIMARY;
            case PRIMARY -> Theme.TEXT_ON_DARK;
            case QUIET   -> Theme.TEXT_MUTED;
        };
    }

    private static Color backgroundColorFor(Variant variant, boolean hovered, boolean pressed) {
        return switch (variant) {
            case DEFAULT -> pressed ? Theme.SURFACE_INSET
                         : hovered ? Theme.SURFACE_MUTED
                         : Theme.BUTTON_SURFACE;
            case PRIMARY -> pressed || hovered ? Theme.ACCENT_TEXT : Theme.ACCENT;
            case QUIET   -> pressed ? Theme.SURFACE_INSET
                         : hovered ? Theme.SURFACE_MUTED
                         : null;
        };
    }
}
