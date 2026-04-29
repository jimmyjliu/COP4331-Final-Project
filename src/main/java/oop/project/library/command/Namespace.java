package oop.project.library.command;

import java.util.Map;

public class Namespace {

    private final Map<String, Object> values;

    public Namespace(Map<String, Object> values) {
        this.values = values;
    }

    public <T> T get(String name, Class<T> type) throws RuntimeException {
        if (!values.containsKey(name)) {
            throw new CommandConfigurationException("Argument " + name + " not found");
        }

        Object value = values.get(name);

        if (!type.isInstance(value)) {
            throw new CommandConfigurationException("Argument '" + name + "' is not of type " + type.getSimpleName());
        }

        return type.cast(value);
    }
}
