package oop.project.library.command;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentParseException;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.HashMap;
import java.util.Map;

public class ArgParser {
    public Namespace parse(Command parent, String rawArguments) throws ArgumentParseException {
        BasicArgs args = tokenizeArguments(rawArguments);
        return parseCommand(parent, args, 0);
    }

    private BasicArgs tokenizeArguments(String arguments) {
        Input lexer = new Input(arguments);
        return lexer.parseBasicArgs();
    }

    /**
     * Recursively parse a command and any nested subcommands starting at a given positional index.
     * Positional Arguments are matched in the order of declaration (first come, first served) until
     * a known subcommand is encountered. Named arguments are then parsed along with default values being applied
     * for both positional and named arguments. Any remaining parts are parsed as a part of the subcommand.
     *
     * @param parent the command definition currently being parsed
     * @param args the tokenized command-line input
     * @param index the starting positional argument index
     * @return a Namespace containing parsed values for this command and any subcommands
     * @throws ArgumentParseException if argument conversion fails, too many positional arguments are supplied,
     *          or required values are missing
     * */
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
            if(pos >= parent.getNumPositional()) {
                // can't nested subcommands within each other
                throw new ArgumentParseException("Expected " + parent.getNumPositional() + " arguments for '" + parent.getProgName() + "' but got " + args.positional().size());
            }
            Argument<?> arg = parent.getAllArguments().get(Integer.toString(pos));
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


    /**
     * Parses all named arguments for the current command and stores converted commands in the parse arguments map
     * If named arg is present without value, the argument's flag-present default is used if applicable.
     *
     * @param parent the command definition currently being parsed
     * @param args the tokenized command-line input
     * @param parsedArgs the map of already parsed arguments for the command
     * @throws ArgumentParseException if unknown named argument is provided, or value cannot be converted to required type
     * */
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

                Argument<?> argument = parent.getAllArguments().get(flag);

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

    /**
     * Applies default values for any missing positional and named arguments of the current command
     *
     * @param parent the command definition currently having defaults applied
     * @param parsedArgs the map of already parsed arguments for the command
     * @param givenPositionalArgs the integer value of the number of Positional Arguments provided by the CLI input
     * @throws ArgumentParseException if a required positional argument is missing
     * */
    private void applyDefault(Command parent, Map<String, Object> parsedArgs, int givenPositionalArgs) {
        // Apply Named Defaults
        for (Argument<?> arg : parent.getAllArguments().values()) {
            if (!parsedArgs.containsKey(arg.getName())) {
                if (arg.hasDefault()) {
                    parsedArgs.put(arg.getName(), arg.getDefault());
                }
            }
        }

        // Apply Positional Defaults
        for (int i = givenPositionalArgs; i < parent.getNumPositional(); i++) {
            Argument<?> arg = parent.getAllArguments().get(Integer.toString(i));

            if (arg.getDefault() != null) {
                parsedArgs.put(arg.getName(), arg.getDefault());
            } else {
                throw new ArgumentParseException("Missing required positional argument " + arg.getName());
            }
        }
    }

    /**
     * Applies the default value associated with a named argument when the flag is present without an explicit value.
     *
     * @param rawValue the supplied flag name
     * @param parent the command which the named argument belongs to
     * @param parsedArgs the parsed argument map to update
     * @throws ArgumentParseException if the supplied flag is not valid for the current command
     * */
    private void applyFlagPresentDefault(String rawValue, Command parent, Map<String, Object> parsedArgs) {
        if(parent.getAllArguments().containsKey(rawValue)) {
            for (Argument<?> arg : parent.getAllArguments().values()) {
                if (!parsedArgs.containsKey(arg.getName())) {
                    if (arg.hasFlagPresentDefault()) {
                        parsedArgs.put(arg.getName(), arg.getFlagPresentDefault());
                    }
                }
            }
        } else {
            throw new ArgumentParseException("Named flag '" + rawValue + "' is not a valid named argument/alias for '" + parent.getProgName() + "'.");
        }
    }
}
