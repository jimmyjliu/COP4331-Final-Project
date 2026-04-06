package oop.project.library.argument;

import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {
    /*
    Ideas:
    like python's argparse, constructor takes in a program/command name
    add_arg method will support arguments for that program/command
    in the argument parser, keep a list of type Arguments?
    allow for refinement of arguments (range)
    */

    String programName;
    ArrayList<Argument<?>> arguments = new ArrayList<>();

    public ArgumentParser(String programName) {
        this.programName = programName;
    }

    public String getProgramName() {
        return programName;
    }

    public ArrayList<Argument<?>> getArguments() {
        return arguments;
    }

    // add argument to parser with specified name and type
    public<T> Argument<T> addArg(String name, Class<T> type) {
        Argument<T> argument = new Argument<>(name, type);
        arguments.add(argument);
        return argument;
    }

    // overloaded addArg for default type of String
    public Argument<String> addArg(String name) {
        return addArg(name, String.class);
    }

    public Namespace parseArgs(String arguments) throws RuntimeException {
        Input lexer = new Input(arguments);

        BasicArgs args = lexer.parseBasicArgs();

        // throw exception if number of positional arguments doesn't match expected number
        if (args.positional().size() != getArguments().size()) {
            throw new RuntimeException("Expected " + getArguments().size() + " positional arguments but got " + args.positional().size());
        }

        Map<String, Object> parsedArgs = new HashMap<>();

        // todo add support for named arguments
        for (int i = 0; i < getArguments().size(); i++) {
            Argument<?> arg = getArguments().get(i);
            String rawValue = args.positional().get(i);

            Object convertedValue;

            try {
                convertedValue = arg.convert(rawValue);
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert argument '" + arg.getName() + "' with value '" + rawValue + "' to type " + arg.getType().getSimpleName(), e);
            }

            parsedArgs.put(arg.getName(), convertedValue);
        }

        return new Namespace(parsedArgs);
    }
}
