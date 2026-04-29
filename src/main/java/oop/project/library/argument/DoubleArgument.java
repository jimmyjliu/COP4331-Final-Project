package oop.project.library.argument;

public class DoubleArgument extends Argument<Double>{
    // optional min/max values for range validation
    private Double minValue;
    private Double maxValue;

    public DoubleArgument(String name) {
        super(name, Double.class);
        parser(Double::parseDouble);
    }

    // method to set range for doubles
    public Argument<Double> range(double min, double max) {
        // runtime, user facing error message
        if (max < min) {
            throw new ArgumentParseException("Invalid range for argument " + getName() + ": max value " + max + " is less than min value " + min);
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
