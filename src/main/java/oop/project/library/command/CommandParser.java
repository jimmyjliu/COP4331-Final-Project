package oop.project.library.command;

import oop.project.library.argument.Argument;

// Facade - hides the complexity and implementation from the user
public class CommandParser {
        private final ArgParser parser;
        private final Command parent;

        public CommandParser(String progName) {
            this.parent = new Command(progName);
            this.parser = new ArgParser();
        }

        public <T> Argument<T> addArgument(Class<T> type, String... dest) {
            return parent.addArgument(type, dest);
        }

        public Argument<String> addArgument(String... dest) {
            return parent.addArgument(String.class, dest);
        }

        public Command addSubCommand(String command, String subProgName) {
            return parent.addSubCommand(command, subProgName);
        }

        public Namespace parseArgs(String arguments) throws RuntimeException {
            return parser.parse(parent, arguments);
        }
}
