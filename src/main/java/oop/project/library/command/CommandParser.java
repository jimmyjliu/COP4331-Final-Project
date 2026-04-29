package oop.project.library.command;

import oop.project.library.argument.*;

// Facade - hides the complexity and implementation from the user
public class CommandParser {
        private final ArgParser parser;
        private final Command parent;

        public CommandParser(String progName) {
            this.parent = new Command(progName);
            this.parser = new ArgParser();
        }

        /**
         * Adding typed argument to the given command
         * @param dest the destination names for the argument; either a single positional name (i.e. checkout)
         *             or one or more named arguments such as -v or --verbose
         * @return the created ArgumentBuilder used to configure the argument
         * */
        public ArgumentBuilder addArgument(String... dest) {
            return parent.addArgument(dest);
        }

        public Command addSubCommand(String command, String subProgName) {
            return parent.addSubCommand(command, subProgName);
        }

        /**
         * Parses the given CLI input for this CommandParser.
         * Subcommands are stored as a nested {@link Namespace} inside parent.
         *
         * @param arguments the raw CLI command arguments provided
         * @return a Namespace containing the parsed arguments values
         * @throws RuntimeException if parsing fails or an argument value is invalid
         * */
        public Namespace parseArgs(String arguments) throws RuntimeException {
            return parser.parse(parent, arguments);
        }
}
