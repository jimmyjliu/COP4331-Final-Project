package oop.project.library.argument;

import java.util.Map;

public class Namespace {

    private final Map<String, Object> values;

    public Namespace(Map<String, Object> values) {
        this.values = values;
    }

    public <T> T get(String name) {
        return (T) values.get(name);
    }
}
