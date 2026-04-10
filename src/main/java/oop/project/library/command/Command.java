package oop.project.library.command;

import oop.project.library.argument.*;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.HashMap;
import java.util.Map;

public class Command {
    private final String progName;
    private final Map<String, Argument<?>> positionalArgs;
    private final Map<String, Argument<?>> namedArgs;

    public Command(String progName) {
        this.progName = progName;
        this.positionalArgs = new HashMap<>();
        this.namedArgs = new HashMap<>();
    }

    public String getProgName() {
        return this.progName;
    }

    public Map<String, Argument<?>> getPositionalArgs() {
        return this.positionalArgs;
    }

    public Map<String, Argument<?>> getNamedArgs() {
        return this.namedArgs;
    }

    // specific type
    public <T> Argument<T> addArgument(Class<T> type, String... dest) {
        if (dest.length == 0) {
            throw new IllegalArgumentException("Arguments cannot be empty");
        }

        // check the string
        // flags:
            // short: a dash and one lower case letter
            // long: two dash, at least one more lower case letters use - to indicate spacing
        if (!(dest[0].matches("-[a-z]|--[a-z]+(-[a-z]+)*"))) {
            // if no double or single flag, positional and there shouldn't be anything after it
            // positional
            Argument<T> argument = new Argument<>(dest[0], type);
            if (dest.length > 1) {
                throw new IllegalArgumentException("Positional arguments cannot be more than one argument");
            }
            this.positionalArgs.put(Integer.toString(positionalArgs.size()), argument);
            return argument;
        }
        // named argument
        // if there is a single or double, could be multiple strings
        // THE FIRST FLAG has precedence
        String name = dest[0].replaceFirst("^-+", "");
        Argument<T> argument = new Argument<>(name, type);
        this.namedArgs.put(argument.getName(), argument);

        for (int i = 1; i < dest.length; i++) {
            if (dest[i].matches("-[a-z]|--[a-z]+(-[a-z]+)*")) {
                name = dest[i].replaceFirst("^-+", "");
                this.namedArgs.put(name, argument);
            } else {
                throw new IllegalArgumentException(dest[i] + "is not a named argument (missing flag notation - or --).");
            }
        }
        return argument;
    }

    // default -> handles it as a string
    public Argument<String> addArgument(String... dest) {
        if (dest.length == 0) {
            throw new IllegalArgumentException("Arguments cannot be empty");
        }

        // check the string
        // flags:
        // short: a dash and one lower case letter
        // long: two dash, at least one more lower case letters use - to indicate spacing
        if (!(dest[0].matches("-[a-z]|--[a-z]+(-[a-z]+)*"))) {
            // if no double or single flag, positional and there shouldn't be anything after it
            // positional
            Argument<String> argument = new Argument<>(dest[0], String.class);
            if (dest.length > 1) {
                throw new IllegalArgumentException("Positional arguments cannot be more than one argument");
            }
            return argument;
        }
        // named argument
        // if there is a single or double, could be multiple strings
        Argument<String> argument = new Argument<>(dest[0], String.class);
        this.namedArgs.put(argument.getName(), argument);

        for (int i = 2; i < dest.length; i++) {
            if (dest[0].matches("-[a-z]|--[a-z]+(-[a-z]+)*")) {
                // need to remove the starting dashes
                String name = dest[i].replaceFirst("^-+", "");
                this.namedArgs.put(name, argument);
            } else {
                throw new IllegalArgumentException(dest[i] + "is not a named argument (missing flag notation - or --).");
            }
        }
        return argument;
    }

    public Namespace parseArgs(String arguments) throws RuntimeException {
        Input lexer = new Input(arguments);

        BasicArgs args = lexer.parseBasicArgs();
//        System.out.println(args.toString());
//        System.out.println("POSITIONAL ARGUMENTS: " + args.positional().toString());
//        System.out.println("NAMED ARGUMENTS: " + args.named().toString());

        // throw exception if number of positional arguments doesn't match expected number
        if (args.positional().size() != getPositionalArgs().size()) {
            throw new ArgumentParseException("Expected " + getPositionalArgs().size() + " positional arguments but got " + args.positional().size());
        }

        Map<String, Object> parsedArgs = new HashMap<>();

        for (int i = 0; i < getPositionalArgs().size(); i++) {
            Argument<?> arg = getPositionalArgs().get(Integer.toString(i));
            String rawValue = args.positional().get(i);

            Object convertedValue;

            try {
                convertedValue = arg.parse(rawValue);
            }  catch (ArgumentParseException | IllegalStateException e) {
                // ArgumentParseException -> CLI/user facing. IllegalStateException -> dev facing (missing parsing function)
                throw e;
            } catch (Exception e) {
                throw new ArgumentParseException("Failed to convert argument '" + arg.getName() + "' with value '" + rawValue + "' to type " + arg.getType().getSimpleName(), e);
            }

            parsedArgs.put(arg.getName(), convertedValue);
        }

        // HAVE A FLAG
        if (!args.named().isEmpty()) {
            // find flag
            for (Map.Entry<String, String> entry : args.named().entrySet()) {
                String flag = entry.getKey();
                String rawValue = entry.getValue();

                Argument<?> argument = getNamedArgs().get(flag);

                if (argument == null) {
                    throw new ArgumentParseException("Named argument '" + flag + "' is not a valid argument.");
                }

                Object convertedValue;
                try {
                    convertedValue = argument.parse(rawValue);
                }  catch (ArgumentParseException | IllegalStateException e) {
                    // ArgumentParseException -> CLI/user facing. IllegalStateException -> dev facing (missing parsing function)
                    throw e;
                } catch (Exception e) {
                    throw new ArgumentParseException("Failed to convert argument '" + argument.getName() + "' with value '" + rawValue + "' to type " + argument.getType().getSimpleName(),
                            e);
                }
                parsedArgs.put(flag, convertedValue);
            }
        }

        return new Namespace(parsedArgs);
    }
}
