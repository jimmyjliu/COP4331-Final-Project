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
#### Good Design Decisions
- The Argument Parse Exception class was a good decisions to help us handle errors that occur within the parsing. This allows us to throw specific errors to the users and 
  identify the issues by being specific.
- The usage of generics seems like a good move as it allows us to avoid having to create multiple children classes and having to override the same parse method. I also think 
  this avoids the issue of having to cast values coming out of the parse function (i.e. no need to box or unbox types).
#### Less-Good Design Decisions
- With the use of Generics, I am slightly concerned for abuse of methods. For example, with range you could do it on a string. Therefore, maybe having a child class would be a 
  better option that way range is only used for numeric types, like double and integer.
### Team Review
One design aspect that we are unsure about is how to handle Enums as argparse4j has effectively 4 ways to handle enums. Instead, we are considering a base method for enums and then a chained option for specifying if the exact Enum or toString() should be used. 