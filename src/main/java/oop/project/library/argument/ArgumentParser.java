package oop.project.library.argument;

import java.util.ArrayList;

public class ArgumentParser {
    /*
    Ideas:
    like python's argparse, constructor takes in a program/command name
    add_arg method will support arguments for that program/command
    in the argument parser, keep a list of type Arguments?
    */

    String programName;
    ArrayList<Argument> arguments = new ArrayList<>();

    public ArgumentParser(String programName) {
        this.programName = programName;
    }

    public void addArg(String name) {
        // add an argument with the given name and default type of String
        arguments.add(new Argument(name));
    }

}
