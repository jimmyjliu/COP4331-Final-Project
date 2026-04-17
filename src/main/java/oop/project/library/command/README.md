# Command System

Handles creation of creation command structures and multi-argument parsing.

## Development Notes
Running log of design decisions, tradeoffs, and other observations.
- Refactored the Command System
  - Now Classes have specific responsibilities and the logic is not intertwined in one class only
  - Command Class
    - Handles Building Commands
      - This is what the user calls to add arguments and build properties of it
    - Adding Arguments and Subcommands
    - Using a map to store the named arguments
      - If a named argument has multiple long and/or short flags, then each will map to the first flag that appeared when arguments were given.
      - The first flag provided takes precedence
      - For the map, it will remove the starting dash (i.e. `-` or `--`) as when the arguments are passed in they only give the name part not the dashes
    - Added having a set to track all the argument names
      - ensures that a user cannot use the same name for positional and named.
      - must be unique. however, a subcommand should be able to use the same as a the parent command
  - ArgParser Class
    - Handles all the parsing logic of the given user CLI input
    - Will set defaults, parse positional, handle subcommands, and parse named
    - Throwing illegal argument exceptions when trying to construct an argument
      - This occurs when multiple positional arguments are given
      - This occurs when a named argument is provided but the following argument is not a named (i.e. positional)
    - parseArgs function
      - has 4 stages
        - handling current commands positional arguments first
          - when a subcommand is found, it breaks out of the loop and continues on
        - handling named arguments
          - inside of here, it will set the short flag defaults
        - applying any defaults
          - this is for non-present positional and named arguments
        - handling subcommand
          - gets to subcommand and runs parseArgs on it
          - will return the namespace of the resulting parse for the subcommand
  - Subparser Class
    - Created when a user wants a subcommand
  - CommandParser Class
    - This is what the user will interact with to create a command
    - Created a default for addArguments
      - The default will handle the type as a string

## MVP Design Ananlysis
### Individual Review (Command Lead)
#### Good Design Decisions
- I finally realized that Command needed to be separated by responsibilities. In the PoC, I had my Command Class doing everything from adding arguments to parsing. Although I 
  was able to implement all the functionality for the MVP under the single class. It just got messy when dealing with subcommand. I realized I should separate responsibilities 
  and have specific classes to handle different jobs. For example, ArgParser handles all the parsing and creating the namespace while Command handles building arguments and 
  storing subcommands. Each class has a purpose and how it interacts as a whole. The user will never interact with ArgParser as they
  just need to call parse from the CommandParser class which then calls the ArgParser functions (makes it simpler for the user and they don't have to understand how everything 
  works to use it). It is now much cleaner and easier to follow how things flow. 
- For subcommands, I created a list to track which commands belong to which subparsers. I thought this was a smart decision as now we can easily identify which subcommand was 
  used per subparser and get all the necessary arguments it may contain. When having a subparser, to get argument values from the subcommand, the user must identify the 
  subcommand called and get it's namespace then they will have to get the necessary arguments. This allows the user to handle different cases where values might be different types.
- Subcommands and commands can have the same argument values. This is a good decision as the two are not necessarily related in terms of the arguments and how they could be 
  used functionally.
#### Bad Design Decisions
- A potentially poor design decision on my end is that `parseCommand(Command parent, BasicArgs args, int index)` takes in an integer value index to know where in the positional 
  arguments of the basic args. This could be considered a poor design choice as I really should not pass in index and set my i value to index. The reason behind this is that it 
  is poor management of handling the positional args list as this index value will control the subcommands state, and we don't likely know when arguments are consumed. This 
  could also introduce a small error where if the indexing is off, then the entire program is off. This actually is a problem I ran into when trying to get the Argument for a 
  positional as for a subcommand, I tried using index but that wasn't the true positional value. Thus, I had to introduce another variable to track the positions of the actual 
  argument not throughout the entire command. I likely could have used like a lexer structure to allow me to peek and pop. However, I did not feel like implementing that and 
  was not really sure whether that was the right approach.
- Potential confusion on how to implement subcommands and chaining of commands. I currently feel like the logic to have mutliple subcommands in use on a single command is not 
  clear. Although you can add subparsers, I am not too confident in whether chaining commands from two subparsers logically works and whether the code accounts for that. Right 
  now, it will throw an error if more positionals follow a given subcommand and there is no way for a subcommand to know whether it belongs to other subparsers' subcommands. I 
  think this would be confusing for users and does not handle the subcommands the best. 
### Individual Review (Argument Lead)

### Team Review
#### Design Decision Disagreement

### Concern to Improve
Subcommands cannot have their own subcommands. I currently believe the design does not permit a subcommand to have subcommands of its own. I am not sure whether argparse
allows that or not. However, I feel like you should be able to nest subcommands within subcommands. The current implementation does not account for that. This probably would
be a good thing to improve on as a lot of CLI command can nest commands.

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