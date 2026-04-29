package oop.project.library.command;

public class CommandConfigurationException extends RuntimeException {
    public CommandConfigurationException(String message) {
        super(message);
    }

    public CommandConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
