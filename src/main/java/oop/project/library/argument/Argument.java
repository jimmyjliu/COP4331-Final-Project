package oop.project.library.argument;

public class Argument<T> {
    // represents an argument for a command. EX: "add 1 2" has two arguments "1" and "2"
    // arguments currently a name and a type that can be specified
    // for methods, return "this" to support chaining of methods

    String name;
    Class<T> type;

    public Argument(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public Argument<T> type(Class<T> type) {
        this.type = type;
        return this; // for chaining
    }
}
