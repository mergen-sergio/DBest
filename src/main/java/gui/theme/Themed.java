package gui.theme;

import java.awt.Component;
import java.awt.Container;
import java.util.function.Supplier;
import javax.swing.JComponent;
import java.awt.Color;

public final class Themed {

    private Themed() {
    }

    private static final String FOREGROUND_KEY = "gui.theme.Themed.foreground";
    private static final String BACKGROUND_KEY = "gui.theme.Themed.background";

    public static <T extends JComponent> T foreground(T component, Supplier<Color> supplier) {
        if (component == null || supplier == null) return component;
        component.putClientProperty(FOREGROUND_KEY, supplier);
        component.setForeground(supplier.get());
        return component;
    }

    public static <T extends JComponent> T background(T component, Supplier<Color> supplier) {
        if (component == null || supplier == null) return component;
        component.putClientProperty(BACKGROUND_KEY, supplier);
        component.setBackground(supplier.get());
        return component;
    }

    public static void refresh(Component root) {
        if (root instanceof JComponent jc) {
            Object fg = jc.getClientProperty(FOREGROUND_KEY);
            if (fg instanceof Supplier<?> supplier) {
                Object value = supplier.get();
                if (value instanceof Color color) {
                    jc.setForeground(color);
                }
            }
            Object bg = jc.getClientProperty(BACKGROUND_KEY);
            if (bg instanceof Supplier<?> supplier) {
                Object value = supplier.get();
                if (value instanceof Color color) {
                    jc.setBackground(color);
                }
            }
        }
        if (root instanceof Container container) {
            for (Component child : container.getComponents()) {
                refresh(child);
            }
        }
    }
}
