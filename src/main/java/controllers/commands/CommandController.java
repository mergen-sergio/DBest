package controllers.commands;

import java.util.*;

public class CommandController {

    private static final int MAX_HISTORY = 200;

    private final List<Command> history;

    /** Called before every UndoableRedoableCommand is executed (used by UndoRedoManager). */
    private static Runnable beforeExecuteHook;

    public static void setBeforeExecuteHook(Runnable hook) {
        beforeExecuteHook = hook;
    }

    public CommandController() {
        this.history = new ArrayList<>();
    }

    public void execute(Command command) {
        if (command == null) return;

        command.execute();

        addToHistory(command);
    }

    public void execute(UndoableRedoableCommand command) {
        if (command == null) return;

        if (beforeExecuteHook != null) beforeExecuteHook.run();

        command.execute();

        addToHistory(command);
    }

    private void addToHistory(Command command) {
        this.history.add(command);
        if (this.history.size() > MAX_HISTORY) {
            this.history.remove(0);
        }
    }

    public List<Command> getHistory() {
        return Collections.unmodifiableList(this.history);
    }
}
