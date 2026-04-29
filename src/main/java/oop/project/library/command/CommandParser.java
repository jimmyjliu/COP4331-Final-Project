package oop.project.library.command;

import oop.project.library.argument.*;

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

        public IntegerArgument addIntegerArgument(String... dest) {
            return parent.addIntegerArgument(dest);
        }

        public DoubleArgument addDoubleArgument(String... dest) {
            return parent.addDoubleArgument(dest);
        }

        public StringArgument addStringArgument(String... dest) {
            return parent.addStringArgument(dest);
        }

        public BooleanArgument addBooleanArgument(String... dest) {
            return parent.addBooleanArgument(dest);
        }

        public <E extends Enum<E>> EnumArgument<E> addEnumArgument(Class<E> type, String... dest) {
            return parent.addEnumArgument(type, dest);
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
