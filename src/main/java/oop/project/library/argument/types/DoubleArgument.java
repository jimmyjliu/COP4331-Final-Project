package oop.project.library.argument.types;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentParseException;

public class DoubleArgument extends Argument<Double> {
    // optional min/max values for range validation
    private Double minValue;
    private Double maxValue;

    public DoubleArgument(String name) {
        super(name, Double.class);
        parser(Double::parseDouble);
    }

    /**
     * Sets a range for valid double values, range inclusive.
     * min must be less than max otherwise an exception will be thrown
     * An ArgumentParseException will be thrown at runtime if the parsed double is outside the specified range.
     *
     * @param min the minimum double
     * @param max the maximum double (inclusive)
     * @return DoubleArgument to allow for method chaining
     * @throws IllegalStateException if the method is passed with a max value less than min
     *
     */
    public DoubleArgument range(double min, double max) {
        // runtime, dev facing error message
        if (max < min) {
            throw new IllegalStateException("Invalid range for argument " + getName() + ": max value " + max + " is less than min value " + min);
        }

        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    @Override
    protected void validate(Double value) {
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
