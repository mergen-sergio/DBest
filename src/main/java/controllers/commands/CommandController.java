package controllers.commands;

import java.util.*;

public class CommandController {

    private static final int MAX_HISTORY = 200;

    private final List<Command> history;

    private final Deque<UndoableRedoableCommand> undos;

    private final Deque<UndoableRedoableCommand> redos;

    /** Called before every UndoableRedoableCommand is executed (used by UndoRedoManager). */
    private static Runnable beforeExecuteHook;

    public static void setBeforeExecuteHook(Runnable hook) {
        beforeExecuteHook = hook;
    }

    public CommandController() {
        this.history = new ArrayList<>();
        this.undos = new ArrayDeque<>();
        this.redos = new ArrayDeque<>();
    }

    public void execute(Command command) {
        if (command == null) return;

        command.execute();

        addToHistory(command);
        this.undos.clear();
        this.redos.clear();
    }

    public void execute(UndoableRedoableCommand command) {
        if (command == null) return;

        if (beforeExecuteHook != null) beforeExecuteHook.run();

        command.execute();

        addToHistory(command);
        this.redos.clear();
        this.undos.push(command);
    }

    public void undo() {
        if (this.undos.isEmpty()) return;

        UndoableRedoableCommand command = this.undos.pop();

        command.undo();

        this.redos.push(command);
        addToHistory(command);
    }

    public void redo() {
        if (this.redos.isEmpty()) return;

        UndoableRedoableCommand command = this.redos.pop();

        command.redo();

        this.undos.push(command);
        addToHistory(command);
    }

    public boolean canUndo() {
        return !this.undos.isEmpty();
    }

    public boolean canRedo() {
        return !this.redos.isEmpty();
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
