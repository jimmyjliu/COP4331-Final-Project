package oop.project.library.argument;

public class BooleanArgument extends Argument<Boolean>{

    public BooleanArgument(String name) {
        super(name, Boolean.class);
        parser(BooleanArgument::parseBoolean);
    }

    // specific boolean parsing (only "true" or "false)
    private static Boolean parseBoolean(String s) {
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        throw new IllegalArgumentException("Expected 'true' or 'false' but got: " + s);
    }

    @Override
    protected void validate(Boolean value) {
        super.validate(value);
    }

}
