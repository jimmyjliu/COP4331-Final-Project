package oop.project.library.argument;

public class IntegerArgument extends Argument<Integer> {
    // optional min/max values for range validation
    private Integer minValue;
    private Integer maxValue;

    public IntegerArgument(String name) {
        super(name, Integer.class);
        parser(Integer::parseInt);
    }

    // method to set range for integers
    public Argument<Integer> range(int min, int max) {
        // runtime, user facing error message
        if (max < min) {
            throw new ArgumentParseException("Invalid range for argument " + getName() + ": max value " + max + " is less than min value " + min);
        }

        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    @Override
    protected void validate(Integer value) {
        super.validate(value);

        if (minValue != null && value < minValue) {
            throw new ArgumentParseException(
                    "Argument '" + getName() + "' must be at least " + minValue
            );
        }

        if (maxValue != null && value > maxValue) {
            throw new ArgumentParseException(
                    "Argument '" + getName() + "' must be at most " + maxValue
            );
        }
    }
}
