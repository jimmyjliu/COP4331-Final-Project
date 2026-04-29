package oop.project.library.command;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentParseException;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.HashMap;
import java.util.Map;

public class ArgParser {
    public Namespace parse(Command parent, String rawArguments) throws RuntimeException {
        BasicArgs args = tokenizeArguments(rawArguments);
        return parseCommand(parent, args, 0);
    }

    private BasicArgs tokenizeArguments(String arguments) {
        Input lexer = new Input(arguments);
        return lexer.parseBasicArgs();
    }

    private Namespace parseCommand(Command parent, BasicArgs args, int index) {
        Map<String, Object> parsedArgs = new HashMap<>();

        // handle parent positional and find any subcommands
        // subcommands will be disguised as positional arguments
        int i = index;
        int pos = 0;
        while(i < args.positional().size()) {
            String posArg = args.positional().get(i);

            if(parent.getSubcommands().containsKey(posArg)) {
                break; // is a subcommand should be parsed as a subcommand
            }
            if(pos >= parent.getPositionalArgs().size()) {
                // can't nested subcommands within each other
                throw new ArgumentParseException("Expected " + parent.getPositionalArgs().size() + " arguments for '" + parent.getProgName() + "' but got " + args.positional().size());
            }
            Argument<?> arg = parent.getPositionalArgs().get(Integer.toString(pos));
            String rawValue = args.positional().get(i);

            Object convertedValue;

            try {
                convertedValue = arg.parse(rawValue);
            }  catch (ArgumentParseException | IllegalStateException e) {
                // ArgumentParseException -> CLI/user facing. IllegalStateException -> dev facing (missing parsing function)
                throw e;
            } catch (Exception e) {
                throw new ArgumentParseException("Failed to convert argument '" + arg.getName() + "' with value '" + rawValue + "' to type " + arg.getType().getSimpleName() + " " +
                        "for command '" + parent.getProgName() + "'.", e);
            }

            parsedArgs.put(arg.getName(), convertedValue);
            i++;
            pos++;
        }
        // Handle Parent Named Arguments
        parseNamedArgs(parent, args, parsedArgs);

        // Apply any parent positional defaults before proceeding to handle subcommand
        applyDefault(parent, parsedArgs, i);

        // Handle Subcommands
        if (i < args.positional().size()) {
            String subName = args.positional().get(i); // subcommand name
            Command sub = parent.getSubcommands().get(subName); // getting the subcommand that exists
            parsedArgs.put(parent.getSubProgName(), subName);

            Namespace parsedSub = parseCommand(sub, args, i + 1);
            // add the Namespace to the parseArgs
            // to extract the values of the subcommand, must get the Namespace first then get the arguments looking for
            parsedArgs.put(subName, parsedSub);
        }
        return new Namespace(parsedArgs);
    }

    private void parseNamedArgs(Command parent, BasicArgs args, Map<String, Object> parsedArgs) {
        if (!args.named().isEmpty()) {
            // find flag
            for (Map.Entry<String, String> entry : args.named().entrySet()) {
                String flag = entry.getKey();
                String rawValue = entry.getValue();

                if(rawValue.isEmpty()) {
                    // apply the short default
                    applyFlagPresentDefault(flag, parent, parsedArgs);
                    break;
                }

                Argument<?> argument = parent.getNamedArgs().get(flag);

                if (argument == null) {
                    throw new ArgumentParseException("Named argument '" + flag + "' is not a valid argument for '" +  parent.getProgName() + "'.");
                }

                Object convertedValue;
                try {
                    // FlagDefault if empty
                    convertedValue = argument.parse(rawValue);
                }  catch (ArgumentParseException | IllegalStateException e) {
                    // ArgumentParseException -> CLI/user facing. IllegalStateException -> dev facing (missing parsing function)
                    throw e;
                } catch (Exception e) {
                    throw new ArgumentParseException("Failed to convert argument '" + argument.getName() + "' with value '" + rawValue + "' to type " + argument.getType().getSimpleName() + " for command '" + parent.getProgName() + "'.", e);
                }
                parsedArgs.put(argument.getName(), convertedValue);
            }
        }
    }

    private void applyDefault(Command parent, Map<String, Object> parsedArgs, int givenPositionalArgs) {
        // Apply Named Defaults
        for (Argument<?> arg : parent.getNamedArgs().values()) {
            if (!parsedArgs.containsKey(arg.getName())) {
                if (arg.hasDefault()) {
                    parsedArgs.put(arg.getName(), arg.getDefault());
                }
            }
        }

        // Apply Positional Defaults
        for (int i = givenPositionalArgs; i < parent.getPositionalArgs().size(); i++) {
            Argument<?> arg = parent.getPositionalArgs().get(Integer.toString(i));

            if (arg.getDefault() != null) {
                parsedArgs.put(arg.getName(), arg.getDefault());
            } else {
                throw new ArgumentParseException("Missing required positional argument " + arg.getName());
            }
        }
    }

    private void applyFlagPresentDefault(String rawValue, Command parent, Map<String, Object> parsedArgs) {
        if(parent.getNamedArgs().containsKey(rawValue)) {
            for (Argument<?> arg : parent.getNamedArgs().values()) {
                if (!parsedArgs.containsKey(arg.getName())) {
                    if (arg.hasShortFlagDefault()) {
                        parsedArgs.put(arg.getName(), arg.getShortFlagDefault());
                    }
                }
            }
        } else {
            throw new ArgumentParseException("Named flag '" + rawValue + "' is not a valid named argument/alias for '" + parent.getProgName() + "'.");
        }
    }
}
