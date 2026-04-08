package oop.project.library.scenarios;

import oop.project.library.command.Command;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
       try {
            Command parse = new Command("mul");
            parse.addArgument(Integer.class, "left");
            parse.addArgument(Integer.class, "right");

            var namespace = parse.parseArgs(arguments);

            Integer left = namespace.get("left", Integer.class);
            Integer right = namespace.get("right", Integer.class);
            return Map.of("left", left, "right", right);
       } catch (RuntimeException e) {
           throw new RuntimeException("Invalid add arguments: " + e.getMessage());
       }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("div");
            parse.addArgument(Double.class, "--left");
            parse.addArgument(Double.class, "--right");

            var namespace = parse.parseArgs(arguments);

            Double left = namespace.get("left", Double.class);
            Double right = namespace.get("right", Double.class);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        try {
            Command parse = new Command("echo");
            parse.addArgument(String.class, "message");

            var namespace = parse.parseArgs(arguments);

            String message = namespace.get("message", String.class);
            return Map.of("message", message);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
        }
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
//        try {
//
//        } catch (RuntimeException e) {
//            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
//        }
        throw new UnsupportedOperationException("TODO (MVP)");
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
//        try {
//
//        } catch (RuntimeException e) {
//            throw new RuntimeException("Invalid add arguments: " + e.getMessage());
//        }
        throw new UnsupportedOperationException("TODO (MVP)");
    }

}
