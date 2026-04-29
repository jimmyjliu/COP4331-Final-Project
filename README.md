# COP4331 Final Project
CLI Arg parsing framework for Java

## Prerequisites
- Java 17+
- Gradle

## Feature Showcase
### Arguments

#### Feature: Strongly typed argument construction & method usage
A key design choice that we chose for the argument system was a type-builder pattern. Originally, we had chosen to use a single Argument<T> class to represent all arguments but found that it was a poor separation of concerns and didn't allow for compile time check of improper argument method usage (IE `.range()` on `String`).

By implementing type subclasses such as `DoubleArgument` and an `ArgumentBuilder` class, we are now able to achieve a fluent style API that provided better separation of argument fields/methods all while guaranteeing compile time safety of proper argument method usage per argument type.

This design choice allows for more readable argument creation and guides the user towards correct usage.

Code Example: Argparse4j
```Java
ArgumentParser parser = ArgumentParsers.newFor("search").build();

// compiles but logically nonsensical. Fails at runtime
parser.addArgument("term")
      .type(String.class)
      .choices(Arguments.range(1, 100));
```

Code Example: Our framework
```Java
CommandParser parser = new CommandParser("search");

// Immediate feedback given to framework user that this is an invalid argument configuration. Does not compile.
parser.addArgument("term")
      .asString()
      .range(1, 100);   // This line fails to compile
```

### Commands
1. Recursive Nested Subcommands

2. Separate Short-Flag Presence Defaults

3. Built-in Validation of Command Configuration

4. 
