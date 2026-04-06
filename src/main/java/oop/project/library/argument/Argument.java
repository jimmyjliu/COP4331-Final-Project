package oop.project.library.argument;

import java.util.function.Function;

public class Argument<T> {
    // represents an argument for a command. EX: "add 1 2" has two arguments "1" and "2"
    // arguments currently a name and a type that can be specified
    // for methods, return "this" to support chaining of methods

    private final String name;
    private final Class<T> type;
    private Function<String, T> converterFunction; // optional custom converter function for parsing

    private T minValue; // optional, for range validation
    private T maxValue;

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

    // range method for numeric types
    public Argument<T> range(T min, T max) {
        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    public Argument<T> parser(Function<String, T> converterFunction) {
        this.converterFunction = converterFunction;
        return this;
    }

    public T convert(String raw) {
        return converterFunction.apply(raw);
    }

    // range validation
    public void validate(T value) {
        if (minValue != null && ((Comparable<T>) value).compareTo(minValue) < 0) {
            throw new IllegalArgumentException("Argument " + name + " must be at least " + minValue);
        }
        if (maxValue != null && ((Comparable<T>) value).compareTo(maxValue) > 0) {
            throw new IllegalArgumentException("Argument " + name + " must be at most " + maxValue);
        }
    }

    // convert string to value and validate
    public T parse(String raw) {
        T value = convert(raw);
        validate(value);
        return value;
    }

    // default converters for common Java types
    private static <T> Function<String, T> defaultConverter(Class<T> type) {
        if (type == String.class) return type::cast;
        if (type == Integer.class || type == int.class) return s -> (T) Integer.valueOf(Integer.parseInt(s));
        if (type == Double.class || type == double.class) return s -> (T) Double.valueOf(Double.parseDouble(s));
        if (type == Boolean.class || type == boolean.class) return s -> (T) Boolean.valueOf(s);

        throw new IllegalArgumentException("No default converter for " + type.getSimpleName());
    }
}
