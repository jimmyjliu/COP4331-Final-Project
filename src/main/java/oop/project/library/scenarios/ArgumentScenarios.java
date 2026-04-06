package oop.project.library.scenarios;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentParser;
import oop.project.library.argument.Namespace;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.function.Supplier;

public final class ArgumentScenarios {

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            ArgumentParser parser = new ArgumentParser("add");
            parser.addArg("left", int.class);
            parser.addArg("right", int.class);

            var namespace = parser.parseArgs(arguments);

            Integer left = namespace.get("left", Integer.class);
            Integer right = namespace.get("right", Integer.class);

            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (PoC)");
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (PoC)");
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (PoC)");
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (PoC)");
    }

}
