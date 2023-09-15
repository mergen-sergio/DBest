package controllers;

import java.awt.Toolkit;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;

import lib.booleanexpression.enums.RelationalOperator;

public class ConstantController {

    private static final String LANGUAGE = "en";

    private static final String COUNTRY = "US";

    private static final Locale CURRENT_LOCALE = new Locale(LANGUAGE, COUNTRY);

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("language.messages", CURRENT_LOCALE);

    public static final Path ROOT_DIRECTORY = Paths.get("").toAbsolutePath();

    public static final List<String> RELATIONAL_OPERATORS = Arrays
        .stream(RelationalOperator.values())
        .map(relationalOperator -> relationalOperator.symbols)
        .flatMap(Arrays::stream)
        .toList();

    public static final String NOT_EQUAL_SYMBOL = "≠";

    public static final String GREATER_THAN_OR_EQUAL_SYMBOL = "≥";

    public static final String LESS_THAN_OR_EQUAL_SYMBOL = "≤";

    public static final String APPLICATION_TITLE = "DBest: Database Basics for Engaging Students and Teachers";

    public static final int TABLE_CELL_HEIGHT = 30;

    public static final int TABLE_CELL_WIDTH = 80;

    public static final int OPERATION_CELL_HEIGHT = 30;

    public static final int OPERATION_CELL_WIDTH = 80;

    public static final String NULL = "null";

    public static final String PK_CSV_TABLE_NAME = "__IDX__";

    public static final CurrentAction NONE_ACTION = new CurrentAction(ActionType.NONE);

    public static final int UI_SCREEN_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.65);

    public static final int UI_SCREEN_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7);

    private ConstantController() {

    }

    public static String getString(String text) {
        return MESSAGES.getString(text);
    }
}
