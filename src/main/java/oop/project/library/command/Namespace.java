package oop.project.library.command;

import java.util.Map;

public class Namespace {

    private final Map<String, Object> values;

    public Namespace(Map<String, Object> values) {
        this.values = values;
    }

    /**
     * Retrieves the value of the argument with the specified name and type. If the argument is not found or is not of the expected type, a CommandConfigurationException is thrown.
     *
     * @param name the name of the argument to retrieve
     * @param type the expected type of the argument value
     * @return the value of the argument cast to the specified type
     * @throws CommandConfigurationException if the argument is not found or is not of the expected
     *
     */
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
