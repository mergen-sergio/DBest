package gui.components;

import gui.theme.Theme;
import gui.theme.Themed;
import gui.theme.Typography;

import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public final class SearchField extends JTextField {

    private static final int FIELD_HEIGHT_PIXELS = 32;
    private static final int ICON_SIZE_PIXELS = 14;
    private static final int LEFT_INSET_PIXELS = Theme.SPACING_LARGE + Theme.SPACING_DEFAULT;
    private static final int RIGHT_EDGE_PADDING_PIXELS = Theme.SPACING_MEDIUM;
    private static final int SHORTCUT_PILL_HORIZONTAL_PADDING = 6;
    private static final int SHORTCUT_PILL_VERTICAL_PADDING = 2;
    private static final int VERTICAL_PADDING_PIXELS = Theme.SPACING_SMALL;

    private final String placeholderText;
    private final String shortcutHintText;
    private final FontIcon magnifyIcon;
    private final int rightInsetPixels;
    private boolean focused = false;

    public SearchField(String placeholderText, String shortcutHintText) {
        super();
        this.placeholderText = placeholderText == null ? "" : placeholderText;
        this.shortcutHintText = shortcutHintText == null ? "" : shortcutHintText;
        this.magnifyIcon = FontIcon.of(MaterialDesignM.MAGNIFY, ICON_SIZE_PIXELS, Theme.TEXT_FAINT);
        this.rightInsetPixels = computeRightInset();

        configureBaseAppearance();
        installFocusListener();
    }

    public void onTextChanged(Consumer<String> handler) {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                handler.accept(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                handler.accept(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                handler.accept(getText());
            }
        });
    }

    public void focusAndSelectAll() {
        requestFocusInWindow();
        selectAll();
    }

    private int computeRightInset() {
        if (shortcutHintText.isEmpty()) {
            return RIGHT_EDGE_PADDING_PIXELS;
        }
        FontMetrics shortcutMetrics = getFontMetrics(Typography.SHORTCUT);
        int textWidth = shortcutMetrics.stringWidth(shortcutHintText);
        return textWidth + (SHORTCUT_PILL_HORIZONTAL_PADDING * 2) + RIGHT_EDGE_PADDING_PIXELS;
    }

    private void configureBaseAppearance() {
        setFont(Typography.BODY);
        Themed.foreground(this, () -> Theme.TEXT_PRIMARY);
        setCaretColor(Theme.ACCENT);
        setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_PADDING_PIXELS,
            LEFT_INSET_PIXELS,
            VERTICAL_PADDING_PIXELS,
            rightInsetPixels));

        Dimension preferred = getPreferredSize();
        setPreferredSize(new Dimension(preferred.width, FIELD_HEIGHT_PIXELS));
    }

    private void installFocusListener() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent event) {
                focused = false;
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

            graphics2D.setColor(Theme.SURFACE);
            graphics2D.fillRoundRect(0, 0, width, height, radius, radius);

            if (focused) {
                graphics2D.setColor(Theme.ACCENT);
                graphics2D.setStroke(new BasicStroke(Theme.STROKE_FOCUS_RING));
                graphics2D.drawRoundRect(1, 1, width - 3, height - 3, radius, radius);
            } else {
                graphics2D.setColor(Theme.BORDER);
                graphics2D.setStroke(new BasicStroke(Theme.STROKE_HAIRLINE));
                graphics2D.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);
            }
        } finally {
            graphics2D.dispose();
        }

        super.paintComponent(graphics);

        paintDecorations(graphics);
    }

    private void paintDecorations(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        try {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int iconY = (getHeight() - ICON_SIZE_PIXELS) / 2;
            magnifyIcon.paintIcon(this, graphics2D, Theme.SPACING_DEFAULT, iconY);

            if (getText().isEmpty() && !placeholderText.isEmpty()) {
                graphics2D.setColor(Theme.TEXT_FAINT);
                graphics2D.setFont(Typography.BODY);
                FontMetrics bodyMetrics = graphics2D.getFontMetrics();
                int baselineY = (getHeight() + bodyMetrics.getAscent()) / 2 - bodyMetrics.getDescent();
                graphics2D.drawString(placeholderText, LEFT_INSET_PIXELS, baselineY);
            }

            if (!shortcutHintText.isEmpty()) {
                FontMetrics shortcutMetrics = graphics2D.getFontMetrics(Typography.SHORTCUT);
                int shortcutTextWidth = shortcutMetrics.stringWidth(shortcutHintText);
                int pillWidth = shortcutTextWidth + (SHORTCUT_PILL_HORIZONTAL_PADDING * 2);
                int pillHeight = shortcutMetrics.getHeight() + (SHORTCUT_PILL_VERTICAL_PADDING * 2);
                int pillX = getWidth() - RIGHT_EDGE_PADDING_PIXELS - pillWidth;
                int pillY = (getHeight() - pillHeight) / 2;

                graphics2D.setColor(Theme.SURFACE_INSET);
                graphics2D.fillRoundRect(pillX, pillY, pillWidth, pillHeight,
                                         Theme.BORDER_RADIUS_SMALL, Theme.BORDER_RADIUS_SMALL);

                graphics2D.setColor(Theme.TEXT_FAINT);
                graphics2D.setFont(Typography.SHORTCUT);
                int textBaselineY = pillY + SHORTCUT_PILL_VERTICAL_PADDING + shortcutMetrics.getAscent();
                graphics2D.drawString(shortcutHintText,
                                      pillX + SHORTCUT_PILL_HORIZONTAL_PADDING,
                                      textBaselineY);
            }
        } finally {
            graphics2D.dispose();
        }
    }
}
