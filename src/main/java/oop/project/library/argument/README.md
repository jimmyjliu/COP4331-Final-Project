# Argument System
Handles parsing a single String input value into typed data.

## Development Notes
TODO: Keep a running log of design decisions, tradeoffs, and other observations.

## PoC Design Analysis

### Individual Review (Argument Lead)

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
