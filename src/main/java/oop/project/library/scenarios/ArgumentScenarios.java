package oop.project.library.scenarios;
import oop.project.library.command.Command;
import java.time.LocalDate;
import java.util.Map;

public final class ArgumentScenarios {

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            Command parser = new Command("add");
            parser.addArgument(int.class, "left");
            parser.addArgument(int.class, "right");

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
            Command parser = new Command("sub");
            parser.addArgument(double.class,"left");
            parser.addArgument(double.class, "right");

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
            Command parser = new Command("fizzbuzz");
            parser.addArgument(int.class, "number").range(1, 100);

            var namespace = parser.parseArgs(arguments);

            Integer number = namespace.get("number", Integer.class);

            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid fizzbuzz arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            Command parser = new Command("difficulty");
            parser.addArgument(String.class, "difficulty").choices("easy", "normal", "hard", "peaceful");

            var namespace = parser.parseArgs(arguments);

            String difficulty = namespace.get("difficulty", String.class);

            return Map.of("difficulty", difficulty);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid difficulty arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            Command parser = new Command("date");
            parser.addArgument(LocalDate.class, "date").parser(LocalDate::parse);

            var namespace = parser.parseArgs(arguments);

            LocalDate date = namespace.get("date", LocalDate.class);

            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid date arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> bool(String arguments) throws RuntimeException {
        try {
            Command parser = new Command("bool");
            parser.addArgument(Boolean.class, "value");

            var namespace = parser.parseArgs(arguments);

            Boolean value = namespace.get("value", Boolean.class);

            return Map.of("value", value);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid bool arguments: " + e.getMessage());
        }
    }

}
