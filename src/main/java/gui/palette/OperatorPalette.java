package gui.palette;

import controllers.ConstantController;
import enums.OperationType;
import gui.components.IconButton;
import gui.components.Popover;
import gui.components.RichTooltip;
import gui.components.SearchField;
import gui.theme.Theme;
import gui.theme.Themed;
import gui.theme.Typography;


import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Window;
import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.MissingResourceException;
import java.util.function.Consumer;

public final class OperatorPalette extends JLayeredPane {

    private static final int PALETTE_WIDTH_PIXELS = 360;
    private static final int GRID_COLUMNS = 3;
    private static final int GRID_CELL_NATURAL_HEIGHT_PIXELS = 92;
    private static final int LIST_SYMBOL_COLUMN_WIDTH_PIXELS = 40;
    private static final int COMPACT_SYMBOL_COLUMN_WIDTH_PIXELS = 32;
    private static final int RECENT_LIMIT = 20;
    private static final int TOOLTIP_HOVER_DELAY_MILLIS = 350;

    private final SearchField searchField;
    private final JScrollPane scrollPane;
    private final JPanel contentArea;
    private final JPanel mainArea;

    private String currentSearchQuery = "";
    private ViewMode currentViewMode = ViewMode.LIST;
    private final Deque<OperationType> recentlyUsedOperators = new ArrayDeque<>();
    private Consumer<OperationType> onOperatorSelected = ignored -> { };

    public OperatorPalette() {
        setPreferredSize(new Dimension(PALETTE_WIDTH_PIXELS, 0));
        setOpaque(true);
        Themed.background(this, () -> Theme.SURFACE);

        this.searchField = new SearchField(
            localizedOr("palette.search.placeholder", "Search operators..."),
            "Ctrl + Space");
        this.contentArea = buildContentArea();
        this.scrollPane = buildScrollPane(contentArea);
        this.mainArea = assembleMainArea(searchField, scrollPane);

        add(mainArea, JLayeredPane.DEFAULT_LAYER);

        installSearchListener();
        installGlobalSearchShortcut();

        applyState();
    }

    public void setOnOperatorSelected(Consumer<OperationType> handler) {
        this.onOperatorSelected = handler == null ? ignored -> { } : handler;
    }

    public ViewMode getCurrentViewMode() {
        return currentViewMode;
    }

    public void setCurrentViewMode(ViewMode mode) {
        if (mode == null || mode == this.currentViewMode) return;
        this.currentViewMode = mode;
        applyState();
    }

    public void focusSearchInput() {
        searchField.focusAndSelectAll();
    }

    private JPanel buildContentArea() {
        JPanel area = new ScrollableContent();
        area.setLayout(new GridBagLayout());
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_SMALL, 0, Theme.SPACING_EXTRA_LARGE * 2, 0));
        return area;
    }

    private JScrollPane buildScrollPane(JPanel content) {
        JScrollPane pane = new JScrollPane(content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        Themed.background(pane.getViewport(), () -> Theme.SURFACE);

        JScrollBar verticalBar = pane.getVerticalScrollBar();
        verticalBar.setUI(new ThemedScrollBarUI());
        verticalBar.setPreferredSize(new Dimension(
            8, 0));
        verticalBar.setOpaque(false);
        verticalBar.setUnitIncrement(16);
        return pane;
    }

    private JPanel assembleMainArea(SearchField search, JScrollPane scroll) {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setOpaque(false);

        JPanel head = new JPanel(new BorderLayout(0, Theme.SPACING_MEDIUM));
        head.setOpaque(false);
        head.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_DEFAULT, Theme.SPACING_DEFAULT,
            Theme.SPACING_MEDIUM, Theme.SPACING_DEFAULT));
        head.add(sectionTitleHeader(), BorderLayout.NORTH);
        head.add(search, BorderLayout.CENTER);

        main.add(head, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private JComponent sectionTitleHeader() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACING_SMALL, 0));

        JLabel label = new JLabel(
            localizedOr("palette.title", "Operators").toUpperCase());
        label.setFont(Typography.CAPTION.deriveFont(Typography.CAPTION.getStyle() | java.awt.Font.BOLD));
        Themed.foreground(label, () -> Theme.TEXT_MUTED);
        row.add(label, BorderLayout.WEST);

        IconButton recentTrigger = new IconButton(
            localizedOr("palette.section.recent", "Recent").toUpperCase(),
            null, IconButton.Variant.QUIET);
        recentTrigger.setToolTipText(localizedOr("palette.section.recent.tooltip",
                "Show recently placed operators"));
        recentTrigger.addActionListener(e -> openRecentWindow(recentTrigger));
        row.add(recentTrigger, BorderLayout.EAST);

        return row;
    }

    private JDialog recentWindow;

    private void openRecentWindow(JComponent anchor) {
        if (recentWindow != null && recentWindow.isDisplayable()) {
            recentWindow.getContentPane().removeAll();
            fillRecentWindowContents(recentWindow);
            recentWindow.revalidate();
            recentWindow.repaint();
            recentWindow.toFront();
            recentWindow.requestFocus();
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        recentWindow = new JDialog(owner,
            localizedOr("palette.section.recent", "Recent"),
            java.awt.Dialog.ModalityType.MODELESS);

        recentWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        recentWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                recentWindow = null;
            }
        });
        recentWindow.setResizable(true);
        fillRecentWindowContents(recentWindow);
        recentWindow.pack();
        recentWindow.setSize(new Dimension(380, 440));
        recentWindow.setLocationRelativeTo(anchor);
        recentWindow.setVisible(true);
    }

    private void fillRecentWindowContents(JDialog dialog) {
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setOpaque(true);
        Themed.background(content, () -> Theme.SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_DEFAULT, Theme.SPACING_DEFAULT,
            Theme.SPACING_DEFAULT, Theme.SPACING_DEFAULT));

        if (recentlyUsedOperators.isEmpty()) {
            JLabel hint = new JLabel(
                localizedOr("palette.section.recent.empty",
                    "Nothing here yet - place an operator on the canvas first."),
                SwingConstants.CENTER);
            hint.setFont(Typography.CAPTION);
            Themed.foreground(hint, () -> Theme.TEXT_FAINT);
            content.add(hint, BorderLayout.CENTER);
        } else {
            JComponent items = buildOperatorsContainer(recentOperatorsAsList());
            JScrollPane scroller = new JScrollPane(items,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroller.setBorder(null);
            scroller.getViewport().setOpaque(true);
            Themed.background(scroller.getViewport(), () -> Theme.SURFACE);
            scroller.setOpaque(true);
            Themed.background(scroller, () -> Theme.SURFACE);
            content.add(scroller, BorderLayout.CENTER);
        }

        dialog.setContentPane(content);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        mainArea.setBounds(0, 0, getWidth(), getHeight());
    }

    private void installSearchListener() {
        searchField.onTextChanged(text -> {
            currentSearchQuery = text;
            applyState();
        });
    }

    private void installGlobalSearchShortcut() {
        KeyStroke shortcut = KeyStroke.getKeyStroke("control SPACE");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(shortcut, "palette.focusSearch");
        getActionMap().put("palette.focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                focusSearchInput();
            }
        });
    }

    private void applyState() {
        contentArea.removeAll();

        boolean isSearchActive = !currentSearchQuery.isBlank();

        GridBagConstraints sectionConstraints = new GridBagConstraints();
        sectionConstraints.gridx = 0;
        sectionConstraints.weightx = 1.0;
        sectionConstraints.weighty = 0.0;
        sectionConstraints.fill = GridBagConstraints.HORIZONTAL;
        sectionConstraints.anchor = GridBagConstraints.NORTH;
        sectionConstraints.gridy = GridBagConstraints.RELATIVE;

        if (isSearchActive) {
            List<OperatorMetadata> matches = OperatorCatalog.matching(currentSearchQuery);
            if (matches.isEmpty()) {
                contentArea.add(buildEmptyState(), sectionConstraints);
            } else {
                contentArea.add(buildSection(
                        localizedOr("palette.section.results", "Results"), matches),
                    sectionConstraints);
            }
        } else {
            for (OperatorCategory category : OperatorCategory.values()) {
                List<OperatorMetadata> operators = OperatorCatalog.byCategory(category);
                if (!operators.isEmpty()) {
                    contentArea.add(buildSection(category.localizedName(), operators),
                        sectionConstraints);
                }
            }
        }

        GridBagConstraints spacerConstraints = new GridBagConstraints();
        spacerConstraints.gridx = 0;
        spacerConstraints.weightx = 1.0;
        spacerConstraints.weighty = 1.0;
        spacerConstraints.fill = GridBagConstraints.BOTH;
        contentArea.add(Box.createGlue(), spacerConstraints);

        contentArea.revalidate();
        contentArea.repaint();
    }


    private JPanel buildSection(String title, List<OperatorMetadata> operators) {
        JPanel section = new JPanel(new BorderLayout(0, 0));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_MEDIUM, 0, 0, 0));

        section.add(buildSectionHeader(title, operators.size()), BorderLayout.NORTH);
        section.add(buildOperatorsContainer(operators), BorderLayout.CENTER);
        return section;
    }

    private JPanel buildSectionHeader(String title, int count) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_SMALL, Theme.SPACING_DEFAULT,
            Theme.SPACING_TIGHT, Theme.SPACING_DEFAULT));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(Typography.CAPTION);
        Themed.foreground(titleLabel, () -> Theme.TEXT_FAINT);

        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(Typography.CAPTION);
        Themed.foreground(countLabel, () -> Theme.TEXT_FAINT);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        return header;
    }

    private JComponent buildOperatorsContainer(List<OperatorMetadata> operators) {
        if (currentViewMode == ViewMode.GRID) {
            return buildGrid(operators);
        }
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);
        for (OperatorMetadata operator : operators) {
            column.add(buildRow(operator));
        }
        return column;
    }

    private JComponent buildGrid(List<OperatorMetadata> operators) {
        int rows = (operators.size() + GRID_COLUMNS - 1) / GRID_COLUMNS;
        int gap = Theme.SPACING_SMALL;

        JPanel grid = new JPanel(new GridLayout(rows, GRID_COLUMNS, gap, gap));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_TIGHT, Theme.SPACING_DEFAULT,
            Theme.SPACING_TIGHT, Theme.SPACING_DEFAULT));
        for (OperatorMetadata operator : operators) {
            grid.add(buildGridCell(operator));
        }
        for (int filler = operators.size(); filler < rows * GRID_COLUMNS; filler++) {
            grid.add(Box.createRigidArea(new Dimension(0, GRID_CELL_NATURAL_HEIGHT_PIXELS)));
        }

        int verticalPadding = Theme.SPACING_TIGHT * 2;
        int totalNaturalHeight = rows * GRID_CELL_NATURAL_HEIGHT_PIXELS
            + (rows - 1) * gap
            + verticalPadding;
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, totalNaturalHeight));
        return grid;
    }

    private JPanel buildRow(OperatorMetadata operator) {
        if (currentViewMode == ViewMode.COMPACT) {
            return buildCompactRow(operator);
        }
        return buildListRow(operator);
    }

    private JPanel buildListRow(OperatorMetadata operator) {
        JPanel row = new InteractiveRow();
        row.setLayout(new BorderLayout(Theme.SPACING_MEDIUM, 0));
        row.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_SMALL + 1, Theme.SPACING_DEFAULT,
            Theme.SPACING_SMALL + 1, Theme.SPACING_DEFAULT));

        JLabel symbolLabel = new JLabel(operator.symbol(), SwingConstants.CENTER);
        symbolLabel.setFont(Typography.ALGEBRA_SYMBOL_MEDIUM);
        Themed.foreground(symbolLabel, () -> Theme.TEXT_PRIMARY);
        symbolLabel.setPreferredSize(new Dimension(LIST_SYMBOL_COLUMN_WIDTH_PIXELS, 24));

        JPanel textColumn = new JPanel();
        textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));
        textColumn.setOpaque(false);

        JLabel nameLabel = new JLabel(operator.displayName());
        nameLabel.setFont(Typography.BODY_EMPHASIZED);
        Themed.foreground(nameLabel, () -> Theme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel(operator.description());
        descriptionLabel.setFont(Typography.CAPTION);
        Themed.foreground(descriptionLabel, () -> Theme.TEXT_MUTED);
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);

        textColumn.add(nameLabel);
        textColumn.add(descriptionLabel);

        JLabel pill = buildCategoryPill(operator.category());

        row.add(symbolLabel, BorderLayout.WEST);
        row.add(textColumn, BorderLayout.CENTER);
        row.add(pill, BorderLayout.EAST);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        attachRowInteractions(row, operator);
        return row;
    }

    private JPanel buildCompactRow(OperatorMetadata operator) {
        JPanel row = new InteractiveRow();
        row.setLayout(new BorderLayout(Theme.SPACING_MEDIUM, 0));
        row.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_TIGHT + 1, Theme.SPACING_DEFAULT,
            Theme.SPACING_TIGHT + 1, Theme.SPACING_DEFAULT));

        JLabel symbolLabel = new JLabel(operator.symbol(), SwingConstants.CENTER);
        symbolLabel.setFont(Typography.ALGEBRA_SYMBOL_SMALL);
        Themed.foreground(symbolLabel, () -> Theme.TEXT_MUTED);
        symbolLabel.setPreferredSize(new Dimension(COMPACT_SYMBOL_COLUMN_WIDTH_PIXELS, 18));

        JLabel nameLabel = new JLabel(operator.displayName());
        nameLabel.setFont(Typography.BODY);
        Themed.foreground(nameLabel, () -> Theme.TEXT_PRIMARY);

        row.add(symbolLabel, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        attachRowInteractions(row, operator);
        return row;
    }

    private JPanel buildGridCell(OperatorMetadata operator) {
        JPanel cell = new InteractiveRow();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(
                Theme.SPACING_DEFAULT, Theme.SPACING_SMALL,
                Theme.SPACING_MEDIUM, Theme.SPACING_SMALL)));
        cell.setAlignmentX(CENTER_ALIGNMENT);

        JLabel symbolLabel = new JLabel(operator.symbol(), SwingConstants.CENTER);
        symbolLabel.setFont(Typography.ALGEBRA_SYMBOL_LARGE);
        Themed.foreground(symbolLabel, () -> Theme.TEXT_PRIMARY);
        symbolLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(
            "<html><div style='text-align: center'>" + operator.displayName() + "</div></html>",
            SwingConstants.CENTER);
        nameLabel.setFont(Typography.CAPTION);
        Themed.foreground(nameLabel, () -> Theme.TEXT_MUTED);
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);

        cell.add(symbolLabel);
        cell.add(Box.createVerticalStrut(Theme.SPACING_SMALL));
        cell.add(nameLabel);

        attachRowInteractions(cell, operator);
        return cell;
    }

    private JLabel buildCategoryPill(OperatorCategory category) {
        JLabel pill = new JLabel(category.localizedName()) {
            @Override
            protected void paintComponent(java.awt.Graphics graphics) {
                java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
                try {
                    graphics2D.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setColor(Theme.SURFACE_MUTED);
                    graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 999, 999);
                } finally {
                    graphics2D.dispose();
                }
                super.paintComponent(graphics);
            }
        };
        pill.setOpaque(false);
        pill.setFont(Typography.CAPTION);
        Themed.foreground(pill, () -> Theme.TEXT_MUTED);
        pill.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_TIGHT, Theme.SPACING_MEDIUM,
            Theme.SPACING_TIGHT + 1, Theme.SPACING_MEDIUM));
        return pill;
    }

    private void attachRowInteractions(JComponent row, OperatorMetadata operator) {
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent event) {
                Themed.background(row, () -> Theme.SURFACE_MUTED);
                row.setOpaque(true);
                row.repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                row.setOpaque(false);
                row.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                onOperatorSelected.accept(operator.type());
            }
        });
    }

    private JComponent buildEmptyState() {
        JPanel empty = new JPanel(new BorderLayout());
        empty.setOpaque(false);
        empty.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_EXTRA_LARGE, Theme.SPACING_DEFAULT,
            Theme.SPACING_EXTRA_LARGE, Theme.SPACING_DEFAULT));

        JLabel symbolLabel = new JLabel("∅", SwingConstants.CENTER);
        symbolLabel.setFont(Typography.ALGEBRA_SYMBOL_LARGE);
        Themed.foreground(symbolLabel, () -> Theme.BORDER_STRONG);

        JLabel messageLabel = new JLabel(
            localizedOr("palette.empty.noResults",
                "No operator matches your search."), SwingConstants.CENTER);
        messageLabel.setFont(Typography.CAPTION);
        Themed.foreground(messageLabel, () -> Theme.TEXT_FAINT);

        empty.add(symbolLabel, BorderLayout.CENTER);
        empty.add(messageLabel, BorderLayout.SOUTH);
        return empty;
    }

    private JComponent buildViewModeOption(ViewMode mode) {
        boolean isActive = (mode == currentViewMode);
        JLabel option = new JLabel(mode.localizedName());
        option.setFont(isActive ? Typography.BODY_EMPHASIZED : Typography.BODY);
        Themed.foreground(option, () -> isActive ? Theme.ACCENT : Theme.TEXT_PRIMARY);
        option.setOpaque(true);
        Themed.background(option, () -> isActive ? Theme.ACCENT_SOFT : Theme.SURFACE);
        option.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACING_SMALL + 1, Theme.SPACING_DEFAULT,
            Theme.SPACING_SMALL + 1, Theme.SPACING_DEFAULT));
        option.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        option.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                currentViewMode = mode;
                Popover.hide();
                applyState();
            }
        });
        return option;
    }

    public void notifyOperatorPlaced(OperationType type) {
        recentlyUsedOperators.remove(type);
        recentlyUsedOperators.addFirst(type);
        while (recentlyUsedOperators.size() > RECENT_LIMIT) {
            recentlyUsedOperators.removeLast();
        }
        applyState();
    }

    private List<OperatorMetadata> recentOperatorsAsList() {
        List<OperatorMetadata> list = new ArrayList<>();
        for (OperationType type : recentlyUsedOperators) {
            for (OperatorMetadata operator : OperatorCatalog.ALL) {
                if (operator.type() == type) {
                    list.add(operator);
                    break;
                }
            }
        }
        return list;
    }

    private static String localizedOr(String key, String fallback) {
        try {
            return ConstantController.getString(key);
        } catch (MissingResourceException missing) {
            return fallback;
        }
    }

    private static final class InteractiveRow extends JPanel {
        InteractiveRow() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics graphics) {
            if (isOpaque()) {
                java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
                try {
                    graphics2D.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setColor(getBackground());
                    graphics2D.fillRect(0, 0, getWidth(), getHeight());
                } finally {
                    graphics2D.dispose();
                }
            }
            super.paintComponent(graphics);
        }
    }

    private static final class ScrollableContent extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return orientation == SwingConstants.VERTICAL ? visible.height : visible.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static final class ThemedScrollBarUI extends BasicScrollBarUI {

        private static final int THUMB_HORIZONTAL_PADDING = 2;

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private static JButton createInvisibleButton() {
            JButton button = new JButton();
            Dimension zero = new Dimension(0, 0);
            button.setPreferredSize(zero);
            button.setMinimumSize(zero);
            button.setMaximumSize(zero);
            return button;
        }

        @Override
        protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {

        }

        @Override
        protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !component.isEnabled()) {
                return;
            }
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            try {
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(thumbColorFor(isDragging, isThumbRollover()));
                int thumbWidth = thumbBounds.width - (THUMB_HORIZONTAL_PADDING * 2);
                graphics2D.fillRoundRect(
                    thumbBounds.x + THUMB_HORIZONTAL_PADDING,
                    thumbBounds.y,
                    thumbWidth,
                    thumbBounds.height,
                    thumbWidth,
                    thumbWidth);
            } finally {
                graphics2D.dispose();
            }
        }

        private static java.awt.Color thumbColorFor(boolean dragging, boolean hovered) {
            if (dragging) {
                return Theme.TEXT_FAINT;
            }
            if (hovered) {
                return Theme.BORDER_STRONG;
            }
            return Theme.BORDER;
        }
    }
}
