package oop.project.library.argument;

public class Argument {
    // represents an argument for a command. EX: "add 1 2" has two arguments "1" and "2"
    // arguments currently a name and a type that can be specified
    // for methods, return "this" to support chaining of methods

    String name;
    Class<?> type;

    public Argument(String name) {
        this.name = name;
        this.type = String.class; // default type is String
    }

    public Argument setType(Class<?> type) {
        this.type = type;
        return this; // for chaining
    }
}
