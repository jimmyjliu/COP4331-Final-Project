package oop.project.library.argument.types;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentParseException;

public class IntegerArgument extends Argument<Integer> {
    // optional min/max values for range validation
    private Integer minValue;
    private Integer maxValue;

    public IntegerArgument(String name) {
        super(name, Integer.class);
        parser(Integer::parseInt);
    }

    /**
     * Sets a range for valid integer values, range inclusive.
     * min must be less than max otherwise an exception will be thrown
     * An ArgumentParseException will be thrown at runtime if the parsed integer is outside the specified range.
     *
     * @param min the minimum integer
     * @param max the maximum integer (inclusive)
     * @return IntegerArgument to allow for method chaining
     * @throws IllegalStateException if the method is passed with a max value less than min
     *
     */
    public IntegerArgument range(int min, int max) {
        // runtime, dev facing error message
        if (max < min) {
            throw new IllegalStateException("Invalid range for argument " + getName() + ": max value " + max + " is less than min value " + min);
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
