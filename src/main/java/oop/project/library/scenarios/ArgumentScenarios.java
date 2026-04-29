package oop.project.library.scenarios;
import oop.project.library.command.CommandParser;
import java.time.LocalDate;
import java.util.Map;

public final class ArgumentScenarios {
    enum Difficulty { PEACEFUL, EASY, NORMAL, HARD };

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("add");
            parser.addArgument("left").asInteger();
            parser.addArgument("right").asInteger();

            var namespace = parser.parseArgs(arguments);

            Integer left = namespace.get("left", Integer.class);
            Integer right = namespace.get("right", Integer.class);

            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) { // todo consider not catching runtime (use something more specific)
            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("sub");
            parser.addArgument("left").asDouble();
            parser.addArgument("right").asDouble();

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
            CommandParser parser = new CommandParser("fizzbuzz");
            parser.addArgument("number").asInteger().range(1, 100);

            var namespace = parser.parseArgs(arguments);

            Integer number = namespace.get("number", Integer.class);

            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid fizzbuzz arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("difficulty");
            parser.addArgument("difficulty").asString().choices("easy", "normal", "hard", "peaceful");

            var namespace = parser.parseArgs(arguments);

            String difficulty = namespace.get("difficulty", String.class);

            return Map.of("difficulty", difficulty);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid difficulty arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> enums(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("enums");
            parser.addArgument("difficulty").asEnum(Difficulty.class).caseSensitive(false);

            var namespace = parser.parseArgs(arguments);

            Difficulty difficulty = namespace.get("difficulty", Difficulty.class);

            return Map.of("difficulty", difficulty);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid difficulty arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("date");
            parser.addArgument("date").as(LocalDate.class).parser(LocalDate::parse);

            var namespace = parser.parseArgs(arguments);

            LocalDate date = namespace.get("date", LocalDate.class);

            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid date arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> bool(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("bool");
            parser.addArgument("value").asBoolean();

            var namespace = parser.parseArgs(arguments);

            Boolean value = namespace.get("value", Boolean.class);

            return Map.of("value", value);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid bool arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> regex(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("regex");
            parser.addArgument("input").asString().regex("[A-Z]+-[IV]+");

            var namespace = parser.parseArgs(arguments);

            String input = namespace.get("input", String.class);

            return Map.of("input", input);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid regex arguments: " + e.getMessage());
        }
    }
}
