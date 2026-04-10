# Command System

Handles creation of creation command structures and multi-argument parsing.

## Development Notes

TODO: Keep a running log of design decisions, tradeoffs, and other observations.

## PoC Design Analysis

### Individual Review (Command Lead)

### Individual Review (Argument Lead)
One well-designed aspect of th Command system is that it incorporates stronger typing when a user add and retrieves an argument. This implementation removes the need for a user to cast types and instead allows the API to handle it. Additionally, the Command system is well-designed in that it throws more specific error message to the user if they use the CLI command incorrectly.

One lesser-well designed aspect is that Command currently stores both verbose and short named flags that point to the same argument. This could cause issues in the future if required arguments are implemented.

### Team Review
