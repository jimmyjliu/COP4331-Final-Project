package oop.project.library.command;

import oop.project.library.argument.Argument;

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

        public Subparser addSubparser() {
            return new Subparser(parent);
        }

        public Subparser addSubparser(String progName) {
            return new Subparser(parent, progName);
        }

        public Namespace parseArgs(String arguments) throws RuntimeException {
            return parser.parse(parent, arguments);
        }
}
