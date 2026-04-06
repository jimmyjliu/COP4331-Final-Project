package oop.project.library.argument;

import java.util.function.Function;

public class Argument<T> {
    // represents an argument for a command. EX: "add 1 2" has two arguments "1" and "2"
    // arguments currently a name and a type that can be specified
    // for methods, return "this" to support chaining of methods

    private final String name;
    private Class<T> type;
    private Function<String, T> converterFunction; // optional custom converter function for parsing

    public Argument(String name, Class<T> type) {
        this.name = name;
        this.type = type;
        this.converterFunction = defaultConverter(type); // set default converter based on type
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public Argument<T> type(Class<T> type) {
        this.type = type;
        return this; // for chaining
    }

    public Argument<T> parser(Function<String, T> converterFunction) {
        this.converterFunction = converterFunction;
        return this; // for chaining
    }

    public T convert(String raw) {
        return converterFunction.apply(raw);
    }

    // default converters for common Java types
    private static <T> Function<String, T> defaultConverter(Class<T> type) {
        if (type == String.class) return s -> type.cast(s);
        if (type == Integer.class || type == int.class) return s -> (T) Integer.valueOf(s);
        if (type == Double.class || type == double.class) return s -> (T) Double.valueOf(s);
        if (type == Boolean.class || type == boolean.class) return s -> (T) Boolean.valueOf(s);

        throw new IllegalArgumentException("No default converter for " + type.getSimpleName());
    }
}
