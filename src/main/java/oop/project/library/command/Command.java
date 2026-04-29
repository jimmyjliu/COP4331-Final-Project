package oop.project.library.command;

import oop.project.library.argument.*;

import java.util.*;

public class Command {
    private final String progName;
    private final Map<String, Argument<?>> positionalArgs;
    private final Map<String, Argument<?>> namedArgs;
    private Map<String, Argument<?>> namedAliases = new HashMap<>();
    private final Map<String, Command> subcommands;
    private final Map<String, String> subcommandParsers;

    private final Set<String> argumentNames;

    public Command(String progName) {
        this.progName = progName;
        this.subcommandParsers = new HashMap<>();
        this.positionalArgs = new HashMap<>();
        this.namedArgs = new HashMap<>();
        this.argumentNames = new HashSet<>();
        this.subcommands = new HashMap<>();
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

    public <T> Argument<T> addArgument(Class<T> type, String... dest) {
        if (dest.length == 0) {
            throw new IllegalArgumentException("Arguments cannot be empty");
        }

        // check the string
        // flags:
            // short: a dash and one lower case letter
            // long: two dash, at least one more lower case letters use - to indicate spacing
        if (!(dest[0].matches("(-|--)[a-z]+(-[a-z]+)*"))) {
            if(argNameExists(dest[0])) {
                // check to ensure name isn't being used already
                throw new IllegalArgumentException("Argument " + dest[0] + " already exists");
            }

            if(!subcommands.isEmpty()) {
                // you can't have subcommands come before positionals
                throw new IllegalStateException("Positional Arguments Must Be Declared Before Adding Any Subcommands.");
            }

            // positional argument
            Argument<T> argument = ArgumentFactory.create(dest[0], type);
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
        Argument<T> argument = ArgumentFactory.create(name, type);
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

    private boolean argNameExists(String name) {
        return this.argumentNames.contains(name);
    }

    public void addSubCommand(String command, String subParserName) {
        this.subcommandParsers.put(command, subParserName);
        Command subCommand = new Command(command);
        this.subcommands.put(command, subCommand);
    }

    public void addSubCommand(Command command, String subParserName) {
        this.subcommandParsers.put(command.getProgName(), subParserName);
        this.subcommands.put(command.getProgName(), command);
    }

    public Map<String, Argument<?>> getNamedAliases() {
        return this.namedAliases;
    }

    public Map<String, Command> getSubcommands() {
        return this.subcommands;
    }

    public Map<String, String> getSubcommandParsers() {
        return this.subcommandParsers;
    }
}
