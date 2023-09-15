package dsl.entities;

public abstract sealed class Command permits Expression, Import, VariableDeclaration {

    private final String command;

    protected Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
