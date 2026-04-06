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
        try {
            ArgumentParser parser = new ArgumentParser("sub");
            parser.addArg("left", double.class);
            parser.addArg("right", double.class);

            var namespace = parser.parseArgs(arguments);

            Double left = namespace.get("left", Double.class);
            Double right = namespace.get("right", Double.class);

            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid sub arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        try {
            ArgumentParser parser = new ArgumentParser("fizzbuzz");
            parser.addArg("number", int.class).range(1, 100);

            var namespace = parser.parseArgs(arguments);

            Integer number = namespace.get("number", Integer.class);

            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid fizzbuzz arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            ArgumentParser parser = new ArgumentParser("difficulty");
            parser.addArg("difficulty", String.class).choices("easy", "normal", "hard", "peaceful");

            var namespace = parser.parseArgs(arguments);

            String difficulty = namespace.get("difficulty", String.class);

            return Map.of("difficulty", difficulty);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid difficulty arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            ArgumentParser parser = new ArgumentParser("date");
            parser.addArg("date", LocalDate.class).parser(LocalDate::parse);

            var namespace = parser.parseArgs(arguments);

            LocalDate date = namespace.get("date", LocalDate.class);

            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid date arguments: " + e.getMessage());
        }
    }

}
