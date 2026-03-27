package controllers.commands;

/**
 * @deprecated Replaced by {@link ConnectNodesCommand}.
 * Kept as an empty class to avoid any stale binary-level references.
 */
@Deprecated
class InsertEdgeCommand extends BaseUndoableRedoableCommand {
    @Override public void execute() {}
    @Override public void undo()    {}
    @Override public void redo()    {}
}
