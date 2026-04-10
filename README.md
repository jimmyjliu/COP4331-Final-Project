# COP4331 Final Project

CLI Arg parsing framework for Java

## Prerequisites

- Java 17+
- Gradle

## Lecture Notes:
### Friday, April 10th:
#### Arguments
- Need to have abstraction, where argument figures out which type to return it as 
- dependent on runtime changes 
  - double parseDouble accepts whitespace 
  - int parseInt does not accept whitespace before -> How does your by-pass this issue?
#### Commands
- throws an errors here 
- how the command class with interpret it
- ADD BETTER ERROR THROWING FOR USERS -> Under ParseArgs
- Validate that named arguments exists and match up -> for Parse Args
  - what happens if named is not require aka use default
```Java
// EXAMPLE:
    public static Object getParsedValueAs(Object untyped, Class<?> expected) throws ArgumentParseException {
        if (expected.isInstance(untyped)) {
            return untyped;
        } else {
            // throw illegal state exception
            throw new ArgumentParseException("Attempts to extract value as type " + expected.getSimpleName() + " but got " + untyped.getClass().getSimpleName());
        }
    }
```
