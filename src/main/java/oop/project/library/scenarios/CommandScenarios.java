package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentParseException;
import oop.project.library.command.*;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
       try {
            Command parse = new Command("mul");
            parse.addArgument("left").asInteger();
            parse.addArgument("right").asInteger();

            var namespace = parse.parseArgs(arguments);

            Integer left = namespace.get("left", Integer.class);
            Integer right = namespace.get("right", Integer.class);
            return Map.of("left", left, "right", right);
       } catch (CommandConfigurationException e) {
           throw new CommandConfigurationException("Invalid mul arguments: " + e.getMessage());
       } catch (ArgumentParseException e) {
           throw new ArgumentParseException("Invalid mul arguments: " + e.getMessage());
       }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("div");
            parse.addArgument("--left").asDouble();
            parse.addArgument("--right").asDouble();

            var namespace = parse.parseArgs(arguments);

            Double left = namespace.get("left", Double.class);
            Double right = namespace.get("right", Double.class);
            return Map.of("left", left, "right", right);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid div arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid div arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("echo");
            parse.addArgument("message").asString().setDefault("echo,echo,echo...");

            var namespace = parse.parseArgs(arguments);

            String message = namespace.get("message", String.class);
            return Map.of("message", message);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid echo arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid echo arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("search");
            parse.addArgument("term").asString();
            parse.addArgument("--case-insensitive", "-i").asBoolean().setDefault(false).setFlagPresentDefault(true);

            var namespace = parse.parseArgs(arguments);
            var term = namespace.get("term", String.class);
            var caseInsensitive = namespace.get("case-insensitive", Boolean.class);

            return Map.of("term", term, "case-insensitive", caseInsensitive);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid search arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid search arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("dispatch");
            var staticType = parse.addSubCommand("static", "type");
            staticType.addArgument("value").asInteger();

            var dynamicType = parse.addSubCommand("dynamic", "type");
            dynamicType.addArgument("value").asString();

            var namespace = parse.parseArgs(arguments);
            var type = namespace.get("type", String.class);
            if (type.equals("static")) {
                var value = namespace.get(type, Namespace.class).get("value", Integer.class);
                return Map.of("type", type, "value", value);
            }
            var value = namespace.get(type, Namespace.class).get("value", String.class);
            return Map.of("type", type, "value", value);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid dispatch arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid dispatch arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> nested(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("nested");
            var staticType = parse.addSubCommand("static", "type");
            staticType.addArgument("value").asString();
            var dynamicType = parse.addSubCommand("dynamic", "type");
            dynamicType.addArgument("value").asString();

            var otro = parse.addSubCommand("otro", "other");
            otro.addArgument("blah").asString();

            parse.addArgument("-flag", "--f");
            var namespace = parse.parseArgs(arguments);

            var otherType = namespace.get("other", String.class);
            var otroValue = namespace.get("otro", Namespace.class).get("blah", String.class);

            return Map.of("other", otherType, "otro",  otroValue);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid nested arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid nested arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> coffee(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("coffee");
            parse.addArgument("--beans").asString().choices("dark", "light", "medium").setDefault("LIGHT").setFlagPresentDefault("none");
            var namespace = parse.parseArgs(arguments);

            var beans = namespace.get("beans", String.class);
            return Map.of("beans", beans);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid nested arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid nested arguments: " + e.getMessage());
        }
    }
}
