package oop.project.library.scenarios;
import oop.project.library.argument.ArgumentParseException;
import oop.project.library.command.CommandConfigurationException;
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
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
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
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("fizzbuzz");
            parser.addArgument("number").asInteger().range(1, 100);

            var namespace = parser.parseArgs(arguments);

            Integer number = namespace.get("number", Integer.class);

            return Map.of("number", number);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("difficulty");
            parser.addArgument("difficulty").asString().choices("easy", "normal", "hard", "peaceful");

            var namespace = parser.parseArgs(arguments);

            String difficulty = namespace.get("difficulty", String.class);

            return Map.of("difficulty", difficulty);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> enums(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("enums");
            parser.addArgument("difficulty").asEnum(Difficulty.class).caseSensitive(false);

            var namespace = parser.parseArgs(arguments);

            Difficulty difficulty = namespace.get("difficulty", Difficulty.class);

            return Map.of("difficulty", difficulty);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("date");
            parser.addArgument("date").as(LocalDate.class).parser(LocalDate::parse);

            var namespace = parser.parseArgs(arguments);

            LocalDate date = namespace.get("date", LocalDate.class);

            return Map.of("date", date);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> bool(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("bool");
            parser.addArgument("value").asBoolean();

            var namespace = parser.parseArgs(arguments);

            Boolean value = namespace.get("value", Boolean.class);

            return Map.of("value", value);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> regex(String arguments) throws RuntimeException {
        try {
            CommandParser parser = new CommandParser("regex");
            parser.addArgument("input").asString().regex("[A-Z]+-[IV]+");

            var namespace = parser.parseArgs(arguments);

            String input = namespace.get("input", String.class);

            return Map.of("input", input);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
        }
    }
}
