package oop.project.library.argument;

import oop.project.library.argument.types.*;
import oop.project.library.command.Command;

public class ArgumentBuilder {
    /*
        A builder class that takes in generic arguments and creates the appropriate subclass for that type argument.
        Allows for a generic "addArgument" method in Command for adding args while restricting methods for certain types of arguments
        (IE range only for numeric types)
     */

    private final Command command;
    private final String[] dest;

    public ArgumentBuilder(Command command, String... dest) {
        this.command = command;
        this.dest = dest;
    }

    public StringArgument asString() {
        return command.addArgumentObject(
                new StringArgument(command.getArgName(dest)),
                dest
        );
    }

    public IntegerArgument asInteger() {
        return command.addArgumentObject(
                new IntegerArgument(command.getArgName(dest)),
                dest
        );
    }

    public DoubleArgument asDouble() {
        return command.addArgumentObject(
                new DoubleArgument(command.getArgName(dest)),
                dest
        );
    }

    public BooleanArgument asBoolean() {
        return command.addArgumentObject(
                new BooleanArgument(command.getArgName(dest)),
                dest
        );
    }

    public <E extends Enum<E>> EnumArgument<E> asEnum(Class<E> type) {
        return command.addArgumentObject(
                new EnumArgument<>(command.getArgName(dest), type),
                dest
        );
    }

    // allow user to pass custom classes as arguments (like LocalDate)
    public <T> Argument<T> as(Class<T> type) {
        return command.addArgumentObject(
                new Argument<>(command.getArgName(dest), type),
                dest
        );
    }
}