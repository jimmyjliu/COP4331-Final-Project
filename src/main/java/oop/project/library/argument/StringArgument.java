package oop.project.library.argument;

import java.util.regex.Pattern;

public class StringArgument extends Argument<String>{
    // optional for regex validation
    private String regex;
    private Pattern pattern;

    public StringArgument(String name) {
        super(name, String.class);
    }

    // method for setting a regex String
    public Argument<String> regex(String s) {
        try {
            pattern = Pattern.compile(s);
            this.regex = s;
            return this;
        }
        catch (Exception e) {
            throw new ArgumentParseException("Invalid regex pattern: " + s);
        }
    }

    @Override
    protected void validate(String value) {
        super.validate(value);

        if (pattern != null && !pattern.matcher(value).matches()) {
            throw new ArgumentParseException(
                    "Argument '" + getName() + "' must match regex: " + regex
            );
        }
    }
}
