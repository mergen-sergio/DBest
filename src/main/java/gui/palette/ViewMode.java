package gui.palette;

import controllers.ConstantController;

import java.util.MissingResourceException;

public enum ViewMode {

    LIST("palette.viewMode.list", "List"),
    GRID("palette.viewMode.grid", "Grid"),
    COMPACT("palette.viewMode.compact", "Compact");

    public final String propertiesKey;
    public final String englishFallback;

    ViewMode(String propertiesKey, String englishFallback) {
        this.propertiesKey = propertiesKey;
        this.englishFallback = englishFallback;
    }

    public String localizedName() {
        try {
            return ConstantController.getString(propertiesKey);
        } catch (MissingResourceException missing) {
            return englishFallback;
        }
    }
}
