package gui.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public final class Theme {

    private Theme() {
    }

    public static Color BACKGROUND;

    public static Color SURFACE;

    public static Color SURFACE_MUTED;

    public static Color SURFACE_INSET;

    public static Color BORDER;

    public static Color BORDER_STRONG;

    public static Color TEXT_PRIMARY;

    public static Color TEXT_MUTED;

    public static Color TEXT_FAINT;

    public static Color TEXT_ON_DARK;

    public static Color ACCENT;

    public static Color ACCENT_SOFT;

    public static Color ACCENT_TEXT;

    public static Color OVERLAY_DARK;

    public static Color CANVAS_TEXT;

    public static Color BUTTON_SURFACE;

    public static final int SPACING_TIGHT = 2;

    public static final int SPACING_SMALL = 4;

    public static final int SPACING_MEDIUM = 8;

    public static final int SPACING_DEFAULT = 12;

    public static final int SPACING_LARGE = 16;

    public static final int SPACING_EXTRA_LARGE = 24;

    public static final int BORDER_RADIUS_SMALL = 4;

    public static final int BORDER_RADIUS_DEFAULT = 6;

    public static final int BORDER_RADIUS_LARGE = 10;

    public static final float STROKE_HAIRLINE = 1.0f;

    public static final float STROKE_FOCUS_RING = 2.0f;

    private static ThemeVariant active;
    private static final List<Runnable> listeners = new ArrayList<>();

    static {
        setActive(ThemeVariant.LIGHT);
    }

    public static ThemeVariant getActive() {
        return active;
    }

    public static void setActive(ThemeVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("variant must not be null");
        }
        active = variant;

        BACKGROUND      = variant.background;
        SURFACE         = variant.surface;
        SURFACE_MUTED   = variant.surfaceMuted;
        SURFACE_INSET   = variant.surfaceInset;
        BORDER          = variant.border;
        BORDER_STRONG   = variant.borderStrong;
        TEXT_PRIMARY    = variant.textPrimary;
        TEXT_MUTED      = variant.textMuted;
        TEXT_FAINT      = variant.textFaint;
        TEXT_ON_DARK    = variant.textOnDark;
        ACCENT          = variant.accent;
        ACCENT_SOFT     = variant.accentSoft;
        ACCENT_TEXT     = variant.accentText;
        OVERLAY_DARK    = variant.overlayDark;
        CANVAS_TEXT     = variant.canvasText;
        BUTTON_SURFACE  = variant.buttonSurface;

        for (Runnable r : new ArrayList<>(listeners)) {
            try {
                r.run();
            } catch (RuntimeException ignored) {
            }
        }

        SwingUtilities.invokeLater(Theme::refreshAllWindows);
    }

    public static void addChangeListener(Runnable listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    public static void removeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static void refreshAllWindows() {
        for (Window w : Window.getWindows()) {
            Themed.refresh(w);
            w.repaint();
        }
    }
}
