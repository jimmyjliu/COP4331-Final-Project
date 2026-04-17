# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

Log of design decisions, tradeoffs, and other observations:
- Chose to use a single Argument class that uses generics to represent all types of arguments
  - This allows for the user to use a more "abstracted" kind of argument system but at the tradeoff of Argument being more complex.
  - Argument\<T\> is also a deeper abstraction for the user compared to having separate classes for each type of argument.
  - Parametric polymorphism was chosen over subtyping polymorphism for our implementation of the argument system.
- Create a custom exception for argument parsing errors (ArgumentParseException) to provide more specific error handling and messaging.
- Allow for chaining of argument parsing and validation methods, similar to argparse4j
- Allow the user to pass in a custom parser function for more complex argument types.
- Add support for enums and introduce case sensitivity option for enums and strings.
- Add support for regex validation for string arguments.
- Add default values and default flag values
- Since Argument system is built with generics, runtime guard rails were introduced for certain methods to ensure that they are only used with appropriate types.
  - range: only applicable for numeric types (int, double, etc)
  - regex: only applicable for String types
  - case sensitivity: only applicable for String and Enum types

## MVP Design Analysis

### Individual Review (Argument Lead)

#### Good Design Decisions
- Introduction of type checking for certain methods (i.e. range only for numeric types, regex only for strings) is a good design decision as it provides guard rails for users and prevents unexpected results of methods.
- Separation of ArgumentParseException and IllegalStateException as the former represents incorrect user/CLI usage while the latter represents incorrect usage of the API by developers. This allows for more specific error handling and messaging.

#### Less-Good Design Decisions
- The use of generics is a double-edge sword as it allows for a more abstracted use of the library but at the cost of compile time checking if certain methods are allowed to be used with certain types.
- The way that Argument is currently structured doesn't scale well for new features and methods. Every new "method" introduces new field members, type checking, and validation logic. While this is manageable for the current scope of the project, it isn't for larger libraries.

### Individual Review (Command Lead)
#### Good Design Decisions
- A good design decision is that the case sensitive can be applied to choices and enums. I think this was a good move as it allows the user to control how the arguments are
  parsed and handled. Having the default be set to true is also helpful as it provides structure and a fallback in case the user doesn't specify how it wants to handle casing.
  This can helpful when the CLI cares about casing or can interpret all versions as valid.
#### Less-Good Design Decisions
- No enforcement for min and max values. In the argument system, we never validate that min must be smaller or equal to max. This could also be a problem when we have choices
  and range being used for numeric types as what happens when I have choices that are not in the range. Is this at fault to the user implement or ours for not flagging this as
  a problem?

### Team Review
#### Design Decision Disagreement
- One disagreement was whether or not Argument should be configured as a builder pattern. Since the complexity has grown and arguments are customizable in nature, there was a disagreement of whether or not the overhead of the builder pattern is worth it.

#### Concern to Improve
- Right now, a user would be able to define a regex and then set case sensitivity to false. However, the case sensitivity doesn't apply to the regex because the regex fails and the case sensitivity never gets "applied". This example demonstrates that there is a concern for the order of operations of methods. Additionally, if case sensitivity should be able to called on regex in the first place since regex inherently deals with casing.

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