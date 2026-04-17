package oop.project.library.command;

public class Subparser {
    private final Command parent;
    private final String progName;

    public Subparser(Command parent) {
        this.parent = parent;
        this.progName = "";
    }

    public Subparser(Command parent, String progName) {
        this.parent = parent;
        this.progName = progName;
    }

    public Command addParser(String name) {
        Command sub = new Command(name);
        this.parent.addSubCommand(sub, this.progName);
        return sub;
    }
}
