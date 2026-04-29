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
    private T defaultFlagValue;
    private boolean defaultFlagValueSet;

    // Custom converter function for parsing. Provided by default for supported argument types
    private Function<String, T> converterFunction;

    // optional for choices validation
    private Set<T> choices;

    public Argument(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    // method to provide a set of valid choices for the argument.
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

    protected void validate(T value) {
        validateChoices(value);
    }

    private void validateChoices(T value) {
        if (choices != null && !choices.contains(value)) {
            throw new ArgumentParseException(
                    "Argument '" + name + "' must be one of " + choices
            );
        }
    }

    // convert string to value and validate, public facing method for parsing argument
    public T parse(String raw) {
        T value = convert(raw);
        validate(value);
        return value;
    }

    public Argument<T> setDefault(T defaultValue) {
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

    public Argument<T> setShortFlagDefault(T defaultValue) {
        this.defaultFlagValue = defaultValue;
        this.defaultFlagValueSet = true;
        return this;
    }

    public T getShortFlagDefault() {
        return defaultFlagValue;
    }

    public boolean hasShortFlagDefault() {
        return defaultFlagValueSet;
    }
}
