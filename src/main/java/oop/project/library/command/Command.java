package oop.project.library.command;

import oop.project.library.argument.*;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.*;

public class Command {
    private final String progName;
    private final Map<String, Argument<?>> positionalArgs;
    private final Map<String, Argument<?>> namedArgs;
    private Map<String, Argument<?>> namedAliases = new HashMap<>();

    private final Set<String> argumentNames;

    public Command(String progName) {
        this.progName = progName;
        this.positionalArgs = new HashMap<>();
        this.namedArgs = new HashMap<>();
        this.argumentNames = new HashSet<>();
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
        if (!(dest[0].matches("(-|--)[a-z]+(-[a-z]+)*"))) {
            if(argNameExists(dest[0])) { // check to ensure name isn't being used already
                throw new IllegalArgumentException("Argument " + dest[0] + " already exists");
            }
            // positional argument
            Argument<T> argument = new Argument<>(dest[0], type);
            if (dest.length > 1) {
                throw new IllegalArgumentException("Positional arguments cannot be more than one argument");
            }
            this.positionalArgs.put(Integer.toString(positionalArgs.size()), argument);
            this.argumentNames.add(argument.getName());
            return argument;
        }

        // Named Argument
        // if there is a single or double, could be multiple strings. THE FIRST FLAG has precedence
        String name = dest[0].replaceFirst("^-+", "");

        if(argNameExists(name)) { // check to ensure name isn't being used already
            throw new IllegalArgumentException("Argument " + name + " already exists");
        }
        Argument<T> argument = new Argument<>(name, type);
        this.namedArgs.put(argument.getName(), argument);
        this.namedAliases.put(argument.getName(), argument);
        this.argumentNames.add(name);

        for (int i = 1; i < dest.length; i++) {
            if (dest[i].matches("(-|--)[a-z]+(-[a-z]+)*")) {
                name = dest[i].replaceFirst("^-+", "");
                if(argNameExists(name)) {
                    throw new IllegalArgumentException("Argument " + name + " already exists");
                }
                this.namedAliases.put(name, argument);
                this.argumentNames.add(name);
            } else {
                throw new IllegalArgumentException(dest[i] + "is not a named argument (missing flag notation - or --).");
            }
        }
        return argument;
    }

    // default -> handles it as a string
    public Argument<String> addArgument(String... dest) {
        return addArgument(String.class, dest);
    }

    private boolean argNameExists(String name) {
        return this.argumentNames.contains(name);
    }

    public Namespace parseArgs(String arguments) throws RuntimeException {
        Input lexer = new Input(arguments);

        BasicArgs args = lexer.parseBasicArgs();
        System.out.println(args.toString());
        System.out.println("POSITIONAL ARGUMENTS: " + args.positional().toString());
        System.out.println("NAMED ARGUMENTS: " + args.named().toString());

        Map<String, Object> parsedArgs = new HashMap<>();

        int givenPositionalArgs = args.positional().size();
        int totalPositionalArgs = getPositionalArgs().size();
        int posDefaults = countPosDefaults();
        int requiredPositionalArgs = totalPositionalArgs - posDefaults;

        if (givenPositionalArgs < requiredPositionalArgs) {
            throw new ArgumentParseException("Expected at least " + requiredPositionalArgs + " positional arguments but got " + givenPositionalArgs);
        }

        if(givenPositionalArgs > totalPositionalArgs) {
            throw new ArgumentParseException("Expected " + totalPositionalArgs + " positional arguments but got " + givenPositionalArgs);
        }

        // Parse USER input positional
        for (int i = 0; i < givenPositionalArgs; i++) {
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

        // Apply Positional Defaults
        for (int i = givenPositionalArgs; i < getPositionalArgs().size(); i++) {
            Argument<?> arg = getPositionalArgs().get(Integer.toString(i));

            if (arg.getDefault() != null) {
                parsedArgs.put(arg.getName(), arg.getDefault());
            } else {
                throw new ArgumentParseException("Missing required positional argument " + arg.getName());
            }
        }

        // HAVE A FLAG
        if (!args.named().isEmpty()) {
            // find flag
            for (Map.Entry<String, String> entry : args.named().entrySet()) {
                String flag = entry.getKey();
                String rawValue = entry.getValue();

                Argument<?> argument = namedAliases.get(flag);

                if (argument == null) {
                    throw new ArgumentParseException("Named argument '" + flag + "' is not a valid argument.");
                }

                Object convertedValue;
                try {
                    // FlagDefault if empty
                    convertedValue = argument.parse(rawValue);
                }  catch (ArgumentParseException | IllegalStateException e) {
                    // ArgumentParseException -> CLI/user facing. IllegalStateException -> dev facing (missing parsing function)
                    throw e;
                } catch (Exception e) {
                    throw new ArgumentParseException("Failed to convert argument '" + argument.getName() + "' with value '" + rawValue + "' to type " + argument.getType().getSimpleName(),
                            e);
                }
                parsedArgs.put(argument.getName(), convertedValue);
            }
        }

        // Apply Named Defaults
        for (Argument<?> arg : namedArgs.values()) {
            if (!parsedArgs.containsKey(arg.getName())) {
                if (arg.hasDefault()) {
                    parsedArgs.put(arg.getName(), arg.getDefault());
                }
            }
        }

        return new Namespace(parsedArgs);
    }

    private int countPosDefaults() {
        int count = 0;
        for (int i = 0; i < getPositionalArgs().size(); i++) {
            Argument<?> arg = getPositionalArgs().get(Integer.toString(i));
            if (arg.hasDefault()) {
                count++;
            }
        }
        return count;
    }
}
