package gui.palette;

import controllers.ConstantController;

import java.util.MissingResourceException;

public enum OperatorCategory {

    ALGEBRA("palette.category.algebra", "Algebra"),
    JOINS("palette.category.joins", "Joins"),
    SETS("palette.category.sets", "Sets"),
    AGGREGATION("palette.category.aggregation", "Aggregation"),
    OTHER("palette.category.other", "Other");

    public final String propertiesKey;
    public final String englishFallback;

    OperatorCategory(String propertiesKey, String englishFallback) {
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
