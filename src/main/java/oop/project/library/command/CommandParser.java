package oop.project.library.command;

import oop.project.library.argument.*;

public class CommandParser {
        private final ArgParser parser;
        private final Command parent;

        public CommandParser(String progName) {
            this.parent = new Command(progName);
            this.parser = new ArgParser();
        }

        public ArgumentBuilder addArgument(String... dest) {
            return parent.addArgument(dest);
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
