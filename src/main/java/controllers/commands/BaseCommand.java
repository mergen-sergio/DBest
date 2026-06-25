package controllers.commands;

abstract class BaseCommand implements Command {

    protected final Class<?> clazz;

    protected BaseCommand() {
        this.clazz = this.getClass();
    }

    @Override
    public String getName() {
        return this.clazz.getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s@%s", this.getName(), Integer.toHexString(System.identityHashCode(this)));
    }
}
