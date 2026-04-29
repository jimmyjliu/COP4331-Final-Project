package oop.project.library.argument.types;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentParseException;

import java.util.regex.Pattern;

public class StringArgument extends Argument<String> {
    // optional for regex validation
    private String regex;
    private Pattern pattern;

    // optional for case insensitivity
    private boolean caseSensitive = true;

    public StringArgument(String name) {
        super(name, String.class);
        parser(String::toString);
    }

    // method for setting a regex String
    public StringArgument regex(String s) {
        try {
            int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
            pattern = Pattern.compile(s, flags);
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
