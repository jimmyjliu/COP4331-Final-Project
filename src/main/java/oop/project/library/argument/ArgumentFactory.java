package oop.project.library.argument;

public class ArgumentFactory {
    public ArgumentFactory() {}

    @SuppressWarnings("unchecked")
    public static <T> Argument<T> create(String name, Class<T> type) {
        if (type == String.class) {
            return (Argument<T>) new StringArgument(name);
        }
        else if (type == Integer.class || type == int.class) {
            return (Argument<T>) new IntegerArgument(name);
        }
        else if (type == Double.class || type == double.class) {
            return (Argument<T>) new DoubleArgument(name);
        }
        else if (type == Boolean.class || type == boolean.class) {
            return (Argument<T>) new BooleanArgument(name);
        }
        else if (type.isEnum()) {
            return (Argument<T>) new EnumArgument(name, type.asSubclass(Enum.class));
        }
        else {
            return (Argument<T>) new Argument(name, type);
        }
    }
}
