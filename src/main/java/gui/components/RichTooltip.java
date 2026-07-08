package gui.components;

import gui.theme.Theme;
import gui.theme.Themed;
import gui.theme.Typography;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.List;
import java.util.WeakHashMap;

public final class RichTooltip {

    private static final int PADDING_PIXELS = Theme.SPACING_DEFAULT;
    private static final int CONTENT_GAP_PIXELS = Theme.SPACING_SMALL;
    private static final int MAX_WIDTH_PIXELS = 260;
    private static final int ANCHOR_GAP_PIXELS = Theme.SPACING_SMALL;
    private static final float TAGS_FONT_SIZE = 10f;

    private static final WeakHashMap<Window, RichTooltip> instancesByOwner = new WeakHashMap<>();
    private static RichTooltip currentlyVisible = null;

    private final JWindow window;
    private final JLabel titleLabel;
    private final JLabel descriptionLabel;
    private final JLabel tagsLabel;

    private RichTooltip(Window owner) {
        this.window = new JWindow(owner);
        this.window.setFocusableWindowState(false);

        this.titleLabel = buildTitleLabel();
        this.descriptionLabel = buildDescriptionLabel();
        this.tagsLabel = buildTagsLabel();

        this.window.setContentPane(buildContentPane());
    }

    public static void showFor(java.awt.Component anchor, String title, String description, List<String> tags) {
        Window owner = SwingUtilities.getWindowAncestor(anchor);
        if (owner == null) {
            return;
        }
        RichTooltip tooltip = instancesByOwner.computeIfAbsent(owner, RichTooltip::new);
        tooltip.applyContent(title, description, tags);
        tooltip.positionNear(anchor);
        tooltip.window.setVisible(true);
        currentlyVisible = tooltip;
    }

    public static void hide() {
        if (currentlyVisible != null) {
            currentlyVisible.window.setVisible(false);
            currentlyVisible = null;
        }
    }

    private void applyContent(String title, String description, List<String> tags) {
        titleLabel.setText(title == null ? "" : title);
        descriptionLabel.setText(formatDescriptionAsHtml(description));
        tagsLabel.setText(formatTagsAsLabel(tags));
        tagsLabel.setVisible(tags != null && !tags.isEmpty());
        window.pack();
    }

    private void positionNear(java.awt.Component anchor) {
        Point anchorLocation = anchor.getLocationOnScreen();
        int anchorWidth = anchor.getWidth();
        int anchorHeight = anchor.getHeight();
        int tooltipWidth = window.getWidth();
        int tooltipHeight = window.getHeight();

        int xToLeft = anchorLocation.x - tooltipWidth - ANCHOR_GAP_PIXELS;
        int xToRight = anchorLocation.x + anchorWidth + ANCHOR_GAP_PIXELS;
        int x = xToLeft >= 0 ? xToLeft : xToRight;

        int y = anchorLocation.y + (anchorHeight - tooltipHeight) / 2;
        if (y < 0) {
            y = anchorLocation.y;
        }

        window.setLocation(x, y);
    }

    private JPanel buildContentPane() {
        JPanel content = new JPanel(new BorderLayout(0, CONTENT_GAP_PIXELS)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                try {
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setColor(Theme.OVERLAY_DARK);
                    graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(),
                                             Theme.BORDER_RADIUS_DEFAULT, Theme.BORDER_RADIUS_DEFAULT);
                } finally {
                    graphics2D.dispose();
                }
                super.paintComponent(graphics);
            }
        };
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
            PADDING_PIXELS, PADDING_PIXELS, PADDING_PIXELS, PADDING_PIXELS));

        JPanel inner = new JPanel(new BorderLayout(0, CONTENT_GAP_PIXELS));
        inner.setOpaque(false);
        inner.add(titleLabel, BorderLayout.NORTH);
        inner.add(descriptionLabel, BorderLayout.CENTER);
        inner.add(tagsLabel, BorderLayout.SOUTH);

        content.add(inner, BorderLayout.CENTER);
        return content;
    }

    private JLabel buildTitleLabel() {
        JLabel label = new JLabel();
        label.setFont(Typography.BODY_EMPHASIZED);
        Themed.foreground(label, () -> Theme.TEXT_ON_DARK);
        return label;
    }

    private JLabel buildDescriptionLabel() {
        JLabel label = new JLabel();
        label.setFont(Typography.BODY);
        Themed.foreground(label, () -> Theme.TEXT_ON_DARK);
        return label;
    }

    private JLabel buildTagsLabel() {
        JLabel label = new JLabel();
        label.setFont(Typography.CAPTION.deriveFont(TAGS_FONT_SIZE));
        Themed.foreground(label, () -> Theme.TEXT_FAINT);
        return label;
    }

    private static String formatDescriptionAsHtml(String description) {
        if (description == null || description.isEmpty()) {
            return "";
        }

        return "<html><body style='width:" + MAX_WIDTH_PIXELS + "px'>" + description + "</body></html>";
    }

    private static String formatTagsAsLabel(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(" · ", tags);
    }
}
