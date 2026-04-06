package oop.project.library.argument;

import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.Map;

public class ArgumentParser {
    /*
    Ideas:
    like python's argparse, constructor takes in a program/command name
    add_arg method will support arguments for that program/command
    in the argument parser, keep a list of type Arguments?
    TODO allow custom parsing (ie LocalDate's parse method)
    allow for refinement of arguments (range)
    */

    String programName;
    ArrayList<Argument<?>> arguments = new ArrayList<>();

    public ArgumentParser(String programName) {
        this.programName = programName;
    }

    public<T> Argument<T> addArg(String name, Class<T> type) {
        // add an argument with the given name and default type of String
        Argument<T> argument = new Argument<>(name, type);
        arguments.add(argument);
        return argument;
    }

    // overloaded addArg for default type of String
    public Argument<String> addArg(String name) {
        return addArg(name, String.class);
    }

    public Map parseArgs(String arguments) throws RuntimeException {
        Input lexer = new Input(arguments);

        BasicArgs args = lexer.parseBasicArgs();

        return null;
    }
}
