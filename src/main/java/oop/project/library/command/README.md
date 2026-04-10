# Command System

Handles creation of creation command structures and multi-argument parsing.

## Development Notes
Running log of design decisions, tradeoffs, and other observations.
- Using a map to store the named arguments
  - If a named argument has multiple long and/or short flags, then each will map to the first flag that appeared when arguments were given.
  - The first flag provided takes precedence
  - For the map, it will remove the starting dash (i.e. `-` or `--`) as when the arguments are passed in they only give the name part not the dashes
- Throwing illegal argument exceptions when trying to construct an argument
  - This occurs when multiple positional arguments are given
  - This occurs when a named argument is provided but the following argument is not a named (i.e. positional)
- Created a default for addArguments
  - The default will handle the type as a string
- parseArgs function
  - throw exception if number of positional arguments doesn't match expected number
  - First will handle positional arguments
    - Will check if it handles the conversion correctly
    - If not, throws an ArgumentParseException
  - Then handles named arguments if any exist
    - Check the mapping
    - Right now it does not confirm that the number of named arguments exist
      - Two Reasons:
        1) The map contains duplicates for the same named argument (i.e. long and short flags that do the same thing)
        2) I believe this will prepare me to handle the case of defaults when that part arrives
      - However, I am considering having another map of total named arguments as how will I know if a default needs to be used or not

## PoC Design Analysis

### Individual Review (Command Lead)
#### Good Design Decisions
- When a named argument has multiple short and long flags, I decided to map it to the same argument.
  - I think this was a good decision as either version of the flag will map to the same argument as the command needs to know what fulfills that argument. Having each map to 
    its own argument would not be a smart choice as then one will always not be set or you would have to find both and set them.
- Storing both positional and named as maps and as separate maps.
  - Having both as maps allow me for positional to know the exact order of the arguments while for named, I know which flags go to what arguments.
  - The maps make it easy to search up flags and identify which argument it relates to. For positional, I could have used an array or list. However, I think the map does what 
    we need it to do.
#### Less Good Design Decisions
- Validate could definitely be strong. For example, adding another check for named arguments, particularly short flags, could be helpful.
  - This ensures that when a new named argument is added that if it is a short flag is holds a standard.
    - i.e. should be one dash and one character

### Individual Review (Argument Lead)
One well-designed aspect of th Command system is that it incorporates stronger typing when a user add and retrieves an argument. This implementation removes the need for a user to cast types and instead allows the API to handle it. Additionally, the Command system is well-designed in that it throws more specific error message to the user if they use the CLI command incorrectly.

One lesser-well designed aspect is that Command currently stores both verbose and short named flags that point to the same argument. This could cause issues in the future if required arguments are implemented.

### Team Review
Currently, we are at a slight disagreement over whether to turn commands into a builder class. argparse4j does not use a builder class but very much has a similar structure by chaining on multiple functionality. Jimmy believes we should do it like argparse4j, while Elle believes it might be better as a builder (note: hasn't looked into the full implementation to be certain of this). 

At this time, we are unsure of how our current design will be impacted by adding defaults. Right now, we think that having a map to the default values could be helpful per flag. When the flag is not included, you find that value. However, we are uncertain how you would keep track of which flags have been called. Also, we are uncertain how to account for resue of flags and how to handle that in the command structure. However, more time will likely need to be spent to understand this situation to know how to handle it.