package gui.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public final class Forms {

    private Forms() {

    }

    public static void onDocumentChange(JTextComponent field, Runnable callback) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                callback.run();
            }
        });
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static int parsePositiveIntOr(String text, int fallback) {
        if (isBlank(text)) return fallback;
        try {
            int n = Integer.parseInt(text.trim());
            return n > 0 ? n : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
