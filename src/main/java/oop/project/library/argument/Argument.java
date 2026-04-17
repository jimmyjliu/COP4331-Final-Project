package oop.project.library.argument;

import java.util.Set;
import java.util.function.Function;

public class Argument<T> {
    /*
         represents an argument for a command. EX: "add 1 2" has two arguments "1" and "2"
         arguments currently must be provided with a name and type upon construction
         other fields can be set with methods
         for methods, return "this" to support chaining of methods
    */

    private final String name;
    private final Class<T> type;
    private T defaultValue;
    private boolean defaultValueSet;

    // optional custom converter function for parsing
    private Function<String, T> converterFunction;

    // optional min/max values for range validation
    private T minValue;
    private T maxValue;

    // optional for choices validation
    private Set<T> choices;

    public Argument(String name, Class<T> type) {
        this.name = name;
        this.type = type;
        this.converterFunction = defaultConverter(type);
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    // method to set range for number types
    // todo consideration: should we enforce that min <= max?
    public Argument<T> range(T min, T max) {
        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    // method to provide a set of valid choices for the argument. Enums are todo
    public Argument<T> choices(T... choices) {
        this.choices = Set.of(choices);
        return this;
    }

    // method to set custom converter function for parsing
    public Argument<T> parser(Function<String, T> converterFunction) {
        this.converterFunction = converterFunction;
        return this;
    }

    // convert raw string to value using converter function
    private T convert(String raw) {
        if (converterFunction == null) {
            throw new IllegalStateException("No converter/parser function set for argument " + name + ". Provide a custom parser using .parser(<function>)" );
        }
        return converterFunction.apply(raw);
    }

    // range & choice validation
    private void validate(T value) {
        if ((minValue != null && ((Comparable<T>) value).compareTo(minValue) < 0)
                || (maxValue != null && ((Comparable<T>) value).compareTo(maxValue) > 0)) {
            throw new ArgumentParseException("Argument " + name + " must be between " + minValue + " and " + maxValue);
        }
        if (choices != null && !choices.contains(value)) {
            throw new ArgumentParseException("Argument " + name + " must be one of " + choices);
        }
    }

    // convert string to value and validate, public facing method for parsing argument
    public T parse(String raw) {
        T value = convert(raw);
        validate(value);
        return value;
    }

    private static Boolean parseBoolean(String s) {
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        throw new IllegalArgumentException("Expected 'true' or 'false' but got: " + s);
    }

    // default converters for common Java types
    private static <T> Function<String, T> defaultConverter(Class<T> type) {
        if (type == String.class) return type::cast;
        if (type == Integer.class || type == int.class) return s -> (T) Integer.valueOf(Integer.parseInt(s));
        if (type == Double.class || type == double.class) return s -> (T) Double.valueOf(Double.parseDouble(s));
        if (type == Boolean.class || type == boolean.class) return s -> (T) parseBoolean(s);

        return null;
    }

    public Argument<T> setDefault(T defaultValue) {
        if (defaultValue.getClass() != this.type) {
            throw new IllegalArgumentException("Default value for argument " + name + " must be of type " + type.getSimpleName());
        }

        this.defaultValue = defaultValue;
        this.defaultValueSet = true;
        return this;
    }

    public T getDefault() {
        return defaultValue;
    }

    public boolean hasDefault() {
        return defaultValueSet;
    }
}
