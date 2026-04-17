package oop.project.library.scenarios;

import oop.project.library.command.*;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
       try {
            CommandParser parse = new CommandParser("mul");
            parse.addArgument(Integer.class, "left");
            parse.addArgument(Integer.class, "right");

            var namespace = parse.parseArgs(arguments);

            Integer left = namespace.get("left", Integer.class);
            Integer right = namespace.get("right", Integer.class);
            return Map.of("left", left, "right", right);
       } catch (RuntimeException e) {
           throw new RuntimeException("Invalid mul arguments: " + e.getMessage());
       }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        try {
            CommandParser parse = new CommandParser("div");
            parse.addArgument(Double.class, "--left");
            parse.addArgument(Double.class, "--right");

            var namespace = parse.parseArgs(arguments);

            Double left = namespace.get("left", Double.class);
            Double right = namespace.get("right", Double.class);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid div arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        try {
            CommandParser parse = new CommandParser("echo");
            parse.addArgument(String.class, "message").setDefault("echo,echo,echo...");

            var namespace = parse.parseArgs(arguments);

            String message = namespace.get("message", String.class);
            return Map.of("message", message);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        try {
            CommandParser parse = new CommandParser("search");
            parse.addArgument(String.class, "term");
            parse.addArgument(Boolean.class, "--case-insensitive", "-i").setDefault(false).setShortFlagDefault(true);

            var namespace = parse.parseArgs(arguments);
            var term = namespace.get("term", String.class);
            var caseInsensitive = namespace.get("case-insensitive", Boolean.class);

            return Map.of("term", term, "case-insensitive", caseInsensitive);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid search arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            CommandParser parse = new CommandParser("dispatch");
            Subparser subcommand = parse.addSubparser("type");
            var staticType = subcommand.addParser("static");
            staticType.addArgument(Integer.class, "value");

            var dynamicType = subcommand.addParser("dynamic");
            dynamicType.addArgument(String.class, "value");

            var namespace = parse.parseArgs(arguments);
            var type = namespace.get("type", String.class);
            if (type.equals("static")) {
                var value = namespace.get(type, Namespace.class).get("value", Integer.class);
                return Map.of("type", type, "value", value);
            }
            var value = namespace.get(type, Namespace.class).get("value", String.class);
            return Map.of("type", type, "value", value);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid dispatch arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> nested(String arguments) throws RuntimeException {
        try {
            CommandParser parse = new CommandParser("nested");
            Subparser subcommand = parse.addSubparser("type");
            var staticType = subcommand.addParser("static");
            staticType.addArgument(String.class, "value");
            var dynamicType = subcommand.addParser("dynamic");
            dynamicType.addArgument(String.class, "value");

            Subparser other = parse.addSubparser("other");
            var otro = other.addParser("otro");
            otro.addArgument(String.class, "blah");

            parse.addArgument("-flag", "--f");
            var namespace = parse.parseArgs(arguments);

            var otherType = namespace.get("other", String.class);
            var otroValue = namespace.get("otro", Namespace.class).get("blah", String.class);

            return Map.of("other", otherType, "otro",  otroValue);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid nested arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> coffee(String arguments) throws RuntimeException {
        try {
            CommandParser parse = new CommandParser("coffee");
            parse.addArgument(String.class, "--beans").choices("dark", "light", "medium").setDefault("LIGHT").setShortFlagDefault("none");
            var namespace = parse.parseArgs(arguments);

            var beans = namespace.get("beans", String.class);
            return Map.of("beans", beans);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid coffee arguments: " + e.getMessage());
        }
    }
}
