package oop.project.library.command;

import oop.project.library.argument.*;

import java.util.*;

public class Command {
    private final String progName;
    private final Command parent;

    private String subProgName = "";
    private final Map<String, Command> subcommands = new HashMap<>();

    private final Map<String, Argument<?>> positionalArgs = new HashMap<>();
    private final Map<String, Argument<?>> namedArgs = new HashMap<>();
    private final Set<String> allArgNames  = new HashSet<>();

    public Command(String progName) {
        this.progName = progName;
        this.parent = null;
    }

    public Command(String progName, Command parent) {
        this.progName = progName;
        this.parent = parent;

    }

    public String getProgName() {
        return this.progName;
    }

    public Command getParent() {
        return parent;
    }

    public String getSubProgName() {
        return this.subProgName;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public Map<String, Command> getSubcommands() {
        return subcommands;
    }

    public Map<String, Argument<?>> getPositionalArgs() {
        return this.positionalArgs;
    }

    public Map<String, Argument<?>> getNamedArgs() {
        return this.namedArgs;
    }

    public <T> Argument<T> addArgument(Class<T> type, String... dest) {
        if (dest.length == 0) {
            throw new CommandConfigurationException("Arguments cannot be empty.");
        }

        // check the string
        // flags:
            // short: a dash and one lower case letter
            // long: two dash, at least one more lower case letters use - to indicate spacing
        if (!isValidFlag(dest[0])) {
            if(argNameExists(dest[0])) {
                // check to ensure name isn't being used already
                throw new CommandConfigurationException("Argument " + dest[0] + " already exists");
            }

            if(!subcommands.isEmpty()) {
                // you can't have subcommands come before positionals
                throw new CommandConfigurationException("Positional Arguments Must Be Declared Before Adding Any Subcommands.");
            }

            // positional argument
            Argument<T> argument = new Argument<>(dest[0], type);
            if (dest.length > 1) {
                throw new CommandConfigurationException("Positional arguments cannot be more than one argument");
            }
            this.positionalArgs.put(Integer.toString(positionalArgs.size()), argument);
            this.allArgNames.add(argument.getName());
            return argument;
        }

        // Named Argument
        // if there is a single or double, could be multiple strings. THE FIRST FLAG has precedence
        String name = dest[0].replaceFirst("^-+", "");

        if(argNameExists(name)) { // check to ensure name isn't being used already
            throw new CommandConfigurationException("Argument " + name + " already exists");
        }
        Argument<T> argument = new Argument<>(name, type);
        this.namedArgs.put(argument.getName(), argument);
        this.allArgNames.add(name);

        for (int i = 1; i < dest.length; i++) {
            if (isValidFlag(dest[i])) {
                name = dest[i].replaceFirst("^-+", "");
                if(argNameExists(name)) {
                    throw new CommandConfigurationException("Argument " + name + " already exists");
                }
                this.namedArgs.put(name, argument);
                this.allArgNames.add(name);
            } else {
                throw new CommandConfigurationException(dest[i] + "is not a named argument (missing flag notation - or --).");
            }
        }
        return argument;
    }

    private boolean argNameExists(String name) {
        return this.allArgNames.contains(name);
    }

    // Subcommands
    public Command addSubCommand(String command, String subParserName) {
        subProgName = subParserName;
        Command child = new Command(command, this);
        subcommands.put(command, child);
        return child;
    }

    // Validation
    private boolean isValidFlag(String flag) {
        return flag.matches("(-|--)[a-z]+(-[a-z]+)*");
    }
}
