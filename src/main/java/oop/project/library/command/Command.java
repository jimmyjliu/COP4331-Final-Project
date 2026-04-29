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

    /**
     * Adding typed argument to the given command
     * @param dest the destination names for the argument; either a single positional name (i.e. checkout)
     *             or one or more named arguments such as -v or --verbose
     * @return the created ArgumentBuilder used to configure the argument
     * */
    public ArgumentBuilder addArgument(String... dest) {
        return new ArgumentBuilder(this, dest);
    }

    public String getArgName(String... dest) {
        if (dest.length == 0) {
            throw new CommandConfigurationException("Arguments cannot be empty.");
        }

        // if named argument, first flag has precedence for the argument being created
        if (isNamedArg(dest[0])) {
            return dest[0].replaceFirst("^-+", "");
        }

        // positional argument
        return dest[0];
    }

    private boolean isNamedArg(String dest) {
        // check the string
        // flags:
            // short: a dash and one lower case letter
            // long: two dash, at least one more lower case letters use - to indicate spacing
        return dest.matches("(-|--)[a-z]+(-[a-z]+)*");
    }

    public <A extends Argument<?>> A addArgumentObject(A argument, String... dest) {
        if (dest.length == 0) {
            throw new CommandConfigurationException("Arguments cannot be empty");
        }

        if (!isNamedArg(dest[0])) {
            // positional argument
            addPositionalArgument(argument, dest);
            return argument;
        }

        // Named Argument
        // if there is a single or double, could be multiple strings. THE FIRST FLAG has precedence
        addNamedArgument(argument, dest);
        return argument;
    }

    /**
     * Adds a positional argument to this command
     * @param argument the positional argument object to be added to the command
     * @param dest the name of the positional argument
     * @throws CommandConfigurationException if the name already exists as an argument,
     *          more than one destination name is supplied, or subcommands have already been added
     * @see #addArgument(String...)
     * */
    private void addPositionalArgument(Argument<?> argument, String... dest) {
        if (argNameExists(dest[0])) {
            // check to ensure name isn't being used already
            throw new CommandConfigurationException("Argument " + dest[0] + " already exists");
        }

        if (!subcommands.isEmpty()) {
            // you can't have subcommands come before positional
            throw new CommandConfigurationException("Positional Arguments Must Be Declared Before Adding Any Subcommands.");
        }

        if (dest.length > 1) {
            throw new CommandConfigurationException("Positional arguments cannot be more than one argument");
        }

        this.positionalArgs.put(Integer.toString(positionalArgs.size()), argument);
        this.allArgNames.add(argument.getName());
    }

    /**
     * Add one or more named arguments to this command
     * @param argument the named argument object to be added to the command
     * @param dest the name(s) of the named argument; can accept multiple names
     * @throws CommandConfigurationException if the name already exists as an argument or
     *          not using flag notation
     * @see #addArgument(String...)
     * */
    private void addNamedArgument(Argument<?> argument, String... dest) {
        // Named Argument
        // if there is a single or double, could be multiple strings. THE FIRST FLAG has precedence
        String name = dest[0].replaceFirst("^-+", "");

        if (argNameExists(name)) {
            // check to ensure name isn't being used already
            throw new CommandConfigurationException("Argument " + name + " already exists");
        }

        this.namedArgs.put(argument.getName(), argument);
        this.allArgNames.add(name);

        for (int i = 1; i < dest.length; i++) {
            if (isNamedArg(dest[i])) {
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
    }

    private boolean argNameExists(String name) {
        // confirms the argument name is not already a part of the command
        return this.allArgNames.contains(name);
    }

    // Subcommands
    public Command addSubCommand(String command, String subParserName) {
        // adds subcommands to the current command
        subProgName = subParserName;
        Command child = new Command(command, this);
        subcommands.put(command, child);
        return child;
    }
}
