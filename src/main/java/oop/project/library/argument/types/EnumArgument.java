package oop.project.library.argument.types;

import oop.project.library.argument.Argument;

public class EnumArgument<E extends Enum<E>> extends Argument<E> {
    private boolean caseSensitive = true;

    public EnumArgument(String name, Class<E> type) {
        super(name, type);
        parser(this::parseEnum);
    }

    public EnumArgument<E> caseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    private E parseEnum(String raw) {
        if (caseSensitive) {
            return Enum.valueOf(getType(), raw);
        }

        for (E constant : getType().getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(raw)) {
                return constant;
            }
        }

        throw new IllegalArgumentException(
                "Invalid value for enum argument '" + getName() + "': " + raw
        );
    }

    @Override
    protected void validate(E value) {
        super.validate(value);
    }
}
