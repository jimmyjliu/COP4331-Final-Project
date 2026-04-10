# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

Log of design decisions, tradeoffs, and other observations:
- Chose to use a single Argument class that uses generics to represent all types of arguments
  - This allows for the user to use a more "abstracted" kind of argument system but at the tradeoff of Argument being more complex.
  - Argument\<T\> is also a deeper abstraction for the user compared to having separate classes for each type of argument.
- Create a custom exception for argument parsing errors (ArgumentParseException) to provide more specific error handling and messaging.
- Allow for chaining of argument parsing and validation methods, similar to argparse4j
- Allow the user to pass in a custom parser function for more complex argument types.

## PoC Design Analysis

### Individual Review (Argument Lead)
The overall API for Arguments is a single class that uses generics to represent all types of arguments. Internally, it stores the type of the argument as a class which is intentional for stronger typing. I believe it works well with the Command system as it allows for stronger compile time typing and for the Command system to enforce such typing easier as well as the type casting is abstracted away within Argument. One lesser-good decision/tradeoff of using generics is that the argument class itself is complex. Other desired functionality to be implemented include support for enums and regex.

### Individual Review (Command Lead)

### Team Review
One design aspect that we are unsure about is how to handle Enums as argparse4j has effectively 4 ways to handle enums. Instead, we are considering a base method for enums and then a chained option for specifying if the exact Enum or toString() should be used. 