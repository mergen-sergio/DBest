package gui.theme;

import java.awt.Font;

public final class Typography {

    private Typography() {
    }

    private static final String SANS_SERIF_FAMILY = Font.DIALOG;
    private static final String SERIF_FAMILY = Font.SERIF;
    private static final String MONOSPACED_FAMILY = Font.MONOSPACED;

    public static final Font CAPTION = new Font(SANS_SERIF_FAMILY, Font.PLAIN, 11);

    public static final Font BODY = new Font(SANS_SERIF_FAMILY, Font.PLAIN, 13);

    public static final Font BODY_EMPHASIZED = new Font(SANS_SERIF_FAMILY, Font.BOLD, 13);

    public static final Font TITLE = new Font(SANS_SERIF_FAMILY, Font.BOLD, 16);

    public static final Font ALGEBRA_SYMBOL_SMALL = new Font(SERIF_FAMILY, Font.PLAIN, 16);

    public static final Font ALGEBRA_SYMBOL_MEDIUM = new Font(SERIF_FAMILY, Font.PLAIN, 20);

    public static final Font ALGEBRA_SYMBOL_LARGE = new Font(SERIF_FAMILY, Font.PLAIN, 26);

    public static final Font SHORTCUT = new Font(MONOSPACED_FAMILY, Font.PLAIN, 11);
}
