package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentParseException;
import oop.project.library.command.Command;
import oop.project.library.command.CommandConfigurationException;
import oop.project.library.command.Namespace;

import java.util.Map;

public final class FeatureShowcaseScenarios {

    public static Map<String, Object> typedArgumentConstruction(String arguments) throws RuntimeException {
        try {
            Command parser = new Command("showcase-search");
            parser.addArgument("term").asString();

            // The README's invalid showcase line is intentionally not executable:
            // parser.addArgument("term").asString().range(1, 100);
            // If you try uncommenting line 18, your IDE will show an error

            var namespace = parser.parseArgs(arguments);
            var term = namespace.get("term", String.class);

            return Map.of("term", term);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid showcase-typed arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid showcase-typed arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> flagPresentDefault(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("showcase-foo");
            parse.addArgument("--dynamic", "-d")
                    .asInteger()
                    .setDefault(30)
                    .setFlagPresentDefault(10);

            var namespace = parse.parseArgs(arguments);
            var dynamic = namespace.get("dynamic", Integer.class);

            return Map.of("dynamic", dynamic);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid showcase-flag arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid showcase-flag arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> nestedSubcommands(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("showcase-dispatch");

            var staticType = parse.addSubCommand("static", "type");
            staticType.addArgument("value").asInteger();

            var dynamicType = parse.addSubCommand("dynamic", "type");
            dynamicType.addArgument("value").asString();

            var namespace = parse.parseArgs(arguments);
            String type = namespace.get("type", String.class);
            Namespace sub = namespace.get(type, Namespace.class);
            Object value = sub.get("value", Object.class);

            return Map.of("type", type, "value", value);
        } catch (CommandConfigurationException e) {
            throw new CommandConfigurationException("Invalid showcase-nested arguments: " + e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid showcase-nested arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> positionalOrderEnforcement(String arguments) throws RuntimeException {
        try {
            // Will throw a runtime error due to improper argument ordering
            Command parse = new Command("showcase-foo");
            parse.addArgument("value").asInteger();

            var boo = parse.addSubCommand("boo", "type");
            boo.addArgument("index").asString();

            parse.addArgument("next").asInteger();
            return Map.of("configured", true);
        } catch (CommandConfigurationException e) {
            return Map.of("error", e.getMessage());
        } catch (ArgumentParseException e) {
            throw new ArgumentParseException("Invalid showcase-order arguments: " + e.getMessage());
        }
    }
}
