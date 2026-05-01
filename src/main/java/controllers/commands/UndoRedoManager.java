package controllers.commands;

import entities.cells.TableCell;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Timeline-based undo/redo manager.
 *
 * Before every canvas-modifying action a {@link CanvasSnapshot} is saved.
 * Ctrl+Z restores the most recently saved snapshot; Ctrl+Y re-applies.
 * At most {@value #MAX_HISTORY} snapshots are kept in each direction to
 * limit memory usage.
 */
public final class UndoRedoManager {

    public static final int MAX_HISTORY = 10;

    private final Deque<CanvasSnapshot> undoStack = new ArrayDeque<>();
    private final Deque<CanvasSnapshot> redoStack = new ArrayDeque<>();

    // -------------------------------------------------------------------

    /** Call this BEFORE performing any user action that modifies the canvas. */
    public void saveSnapshot() {
        pushSnapshot(CanvasSnapshot.capture());
    }

    /** Pushes an already-captured snapshot onto the undo stack (e.g. captured before a drag). */
    public void pushSnapshot(CanvasSnapshot snapshot) {
        redoStack.clear();
        undoStack.push(snapshot);
        while (undoStack.size() > MAX_HISTORY) {
            undoStack.pollLast(); // drop oldest
        }
        notifyStateChanged();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Restores the previous canvas state.
     *
     * @param tables The live table registry used to re-add table cells.
     */
    public void undo(Map<String, TableCell> tables) {
        if (undoStack.isEmpty()) return;
        redoStack.push(CanvasSnapshot.capture()); // current state → redo
        undoStack.pop().restore(tables);
        notifyStateChanged();
    }

    /**
     * Re-applies the next canvas state (after an undo).
     *
     * @param tables The live table registry.
     */
    public void redo(Map<String, TableCell> tables) {
        if (redoStack.isEmpty()) return;
        undoStack.push(CanvasSnapshot.capture()); // current state → undo
        redoStack.pop().restore(tables);
        notifyStateChanged();
    }

    /** Discards all history (e.g. when the canvas is fully cleared). */
    public void reset() {
        undoStack.clear();
        redoStack.clear();
        notifyStateChanged();
    }

    public int undoCount() { return undoStack.size(); }
    public int redoCount() { return redoStack.size(); }

    private Runnable onStateChanged;

    public void setOnStateChanged(Runnable callback) {
        this.onStateChanged = callback;
    }

    private void notifyStateChanged() {
        if (onStateChanged != null) onStateChanged.run();
    }
}
