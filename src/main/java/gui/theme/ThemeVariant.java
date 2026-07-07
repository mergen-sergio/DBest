package gui.theme;

import controllers.ConstantController;

import java.awt.Color;

public enum ThemeVariant {

    LIGHT(
        "theme.variant.light",
        /* background     */ new Color(0xFAFAF7),
        /* surface        */ new Color(0xFFFFFF),
        /* surfaceMuted   */ new Color(0xF4F3EF),
        /* surfaceInset   */ new Color(0xEFEEE9),
        /* border         */ new Color(0xE5E4DF),
        /* borderStrong   */ new Color(0xCFCDC5),
        /* textPrimary    */ new Color(0x1B1B1A),
        /* textMuted      */ new Color(0x6E6D67),
        /* textFaint      */ new Color(0xA6A49C),
        /* textOnDark     */ new Color(0xF4F3EF),
        /* accent         */ new Color(0x2D5F4F),
        /* accentSoft     */ new Color(0xE5EFEC),
        /* accentText     */ new Color(0x1E4538),
        /* overlayDark    */ new Color(0x1B1B1A),
        /* canvasText     */ new Color(0x1B1B1A),
        /* buttonSurface  */ new Color(0xFFFFFF)
    ),

    COFFEE(
        "theme.variant.coffee",
        /* background     */ new Color(0xF1E8D2),
        /* surface        */ new Color(0xFBF6E7),
        /* surfaceMuted   */ new Color(0xEADFC2),
        /* surfaceInset   */ new Color(0xE1D5B0),
        /* border         */ new Color(0xD9C9A6),
        /* borderStrong   */ new Color(0xC1AE85),
        /* textPrimary    */ new Color(0x3E2E17),
        /* textMuted      */ new Color(0x6B563A),
        /* textFaint      */ new Color(0x9A886A),
        /* textOnDark     */ new Color(0xFBF6E7),
        /* accent         */ new Color(0x8B5A2B),
        /* accentSoft     */ new Color(0xE9D5AE),
        /* accentText     */ new Color(0x5A3A18),
        /* overlayDark    */ new Color(0x3E2E17),
        /* canvasText     */ new Color(0x3E2E17),
        /* buttonSurface  */ new Color(0xFBF6E7)
    ),

    DARK(
        "theme.variant.dark",
        /* background     */ new Color(0x1E1E1E),
        /* surface        */ new Color(0x252525),
        /* surfaceMuted   */ new Color(0x2A2A2A),
        /* surfaceInset   */ new Color(0x1A1A1A),
        /* border         */ new Color(0x3A3A3A),
        /* borderStrong   */ new Color(0x555555),
        /* textPrimary    */ new Color(0xF0F0EE),
        /* textMuted      */ new Color(0xB0AFA9),
        /* textFaint      */ new Color(0x7A7975),
        /* textOnDark     */ new Color(0xF0F0EE),
        /* accent         */ new Color(0xFFB86C),
        /* accentSoft     */ new Color(0x4A3820),
        /* accentText     */ new Color(0xFFCF95),
        /* overlayDark    */ new Color(0x0F0F0F),
        /* canvasText     */ new Color(0xF5D673),
        /* buttonSurface  */ new Color(0x383838)
    ),

    MIDNIGHT(
        "theme.variant.midnight",
        /* background     */ new Color(0x000000),
        /* surface        */ new Color(0x0A0A0A),
        /* surfaceMuted   */ new Color(0x111111),
        /* surfaceInset   */ new Color(0x080808),
        /* border         */ new Color(0x1A1A1A),
        /* borderStrong   */ new Color(0x333333),
        /* textPrimary    */ new Color(0xD6D6D2),
        /* textMuted      */ new Color(0x8A8A85),
        /* textFaint      */ new Color(0x555552),
        /* textOnDark     */ new Color(0xD6D6D2),
        /* accent         */ new Color(0xFFC97A),
        /* accentSoft     */ new Color(0x3D2E1A),
        /* accentText     */ new Color(0xFFD9A0),
        /* overlayDark    */ new Color(0x000000),
        /* canvasText     */ new Color(0xFFE79A),
        /* buttonSurface  */ new Color(0x1F1F1F)
    );

    private final String i18nKey;
    public final Color background;
    public final Color surface;
    public final Color surfaceMuted;
    public final Color surfaceInset;
    public final Color border;
    public final Color borderStrong;
    public final Color textPrimary;
    public final Color textMuted;
    public final Color textFaint;
    public final Color textOnDark;
    public final Color accent;
    public final Color accentSoft;
    public final Color accentText;
    public final Color overlayDark;
    public final Color canvasText;
    public final Color buttonSurface;

    ThemeVariant(
        String i18nKey,
        Color background, Color surface, Color surfaceMuted, Color surfaceInset,
        Color border, Color borderStrong,
        Color textPrimary, Color textMuted, Color textFaint, Color textOnDark,
        Color accent, Color accentSoft, Color accentText,
        Color overlayDark,
        Color canvasText,
        Color buttonSurface
    ) {
        this.i18nKey = i18nKey;
        this.background = background;
        this.surface = surface;
        this.surfaceMuted = surfaceMuted;
        this.surfaceInset = surfaceInset;
        this.border = border;
        this.borderStrong = borderStrong;
        this.textPrimary = textPrimary;
        this.textMuted = textMuted;
        this.textFaint = textFaint;
        this.textOnDark = textOnDark;
        this.accent = accent;
        this.accentSoft = accentSoft;
        this.accentText = accentText;
        this.overlayDark = overlayDark;
        this.canvasText = canvasText;
        this.buttonSurface = buttonSurface;
    }

    public String getDisplayName() {
        return ConstantController.getString(i18nKey);
    }

    @Deprecated
    public String displayName() {
        return getDisplayName();
    }
}
