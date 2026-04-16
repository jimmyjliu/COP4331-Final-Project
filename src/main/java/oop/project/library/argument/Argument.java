package oop.project.library.argument;

import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Argument<T> {
    /*
         represents an argument for a command. EX: "add 1 2" has two arguments "1" and "2"
         arguments currently must be provided with a name and type upon construction
         other fields can be set with methods
         for methods, return "this" to support chaining of methods
    */

    private final String name;
    private final Class<T> type;

    // optional custom converter function for parsing
    private Function<String, T> converterFunction;

    // optional min/max values for range validation
    private T minValue;
    private T maxValue;

    // optional for choices validation
    private Set<T> choices;

    // optional for choices and enums case sensitivity (sensitive by default)
    private boolean caseSensitive = true;

    // optional for regex validation (for string types)
    private String regex;
    private Pattern pattern;

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
        // runtime, dev facing error message
        if (!isNumericType()) {
            throw new IllegalStateException("Range validation is only applicable for numeric types. Argument " + name + " is of type " + type.getSimpleName());
        }
        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    // method to provide a set of valid choices for the argument.
    public Argument<T> choices(T... choices) {
        this.choices = Set.of(choices);
        return this;
    }

    // method to set case sensitivity for choices and enums, default is true (case sensitive)
    public Argument<T> caseSensitive(boolean caseSensitive) {
        if (!supportsCaseSensitivity()) {
            throw new IllegalStateException("Case sensitivity is only applicable for String and enum types. Argument " + name + " is of type " + type.getSimpleName());
        }
        this.caseSensitive = caseSensitive;
        return this;
    }

    // method for setting a regex String
    public Argument<T> regex(String s) {
        if (this.type != String.class) {
            throw new IllegalStateException("Regex validation is only applicable for String types. Argument " + name + " is of type " + type.getSimpleName());
        }
        try {
            pattern = Pattern.compile(s);
            this.regex = s;
            return this;
        }
        catch (Exception e) {
            throw new ArgumentParseException("Invalid regex pattern: " + s);
        }
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
        if (choices != null && !validateChoices(value)) {
            throw new ArgumentParseException("Argument " + name + " must be one of " + choices);
        }
        if (regex != null) {
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.matches()) {
                throw new ArgumentParseException("Argument " + name + " must match regex: " + regex);
            }
        }
    }

    private boolean validateChoices(T value) {
        if (caseSensitive) {
            return choices.contains(value);
        }

        for (T choice : choices) {
            if (choice.toString().equalsIgnoreCase(value.toString())) {
                return true;
            }
        }
        return false;
    }

    // convert string to value and validate, public facing method for parsing argument
    public T parse(String raw) {
        T value = convert(raw);
        validate(value);
        return value;
    }

    // numeric type helper
    private boolean isNumericType() {
        return Number.class.isAssignableFrom(type)
                || type == int.class
                || type == double.class
                || type == long.class
                || type == float.class
                || type == short.class
                || type == byte.class;
    }

    // case sensitivity helper
    private boolean supportsCaseSensitivity() {
        return type == String.class || type.isEnum();
    }

    private static Boolean parseBoolean(String s) {
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        throw new IllegalArgumentException("Expected 'true' or 'false' but got: " + s);
    }

    private <T extends Enum<T>> T parseEnum(Class<T> type, String s) {
        if (caseSensitive) {
            return Enum.valueOf(type, s);
        }

        for (T constant : type.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(s)) {
                return constant;
            }
        }

        throw new IllegalArgumentException(
                "Invalid value '" + s + "' for enum (case sensitive) " + type.getSimpleName()
        );
    }

    // default converters for common Java types
    private <T> Function<String, T> defaultConverter(Class<T> type) {
        if (type == String.class) return type::cast;
        if (type == Integer.class || type == int.class) return s -> (T) Integer.valueOf(Integer.parseInt(s));
        if (type == Double.class || type == double.class) return s -> (T) Double.valueOf(Double.parseDouble(s));
        if (type == Boolean.class || type == boolean.class) return s -> (T) parseBoolean(s);
        if (type.isEnum()) return s -> (T) parseEnum((Class<? extends Enum>) type, s);

        return null;
    }
}
