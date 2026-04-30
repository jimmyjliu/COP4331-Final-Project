# COP4331 Final Project
CLI Arg parsing framework for Java

## Prerequisites
- Java 17+
- Gradle

## How to Use the Library

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
#### 1. Recursive Nested Subcommands
When dealing with subcommands, instead of using a flat layer mapping to map all parsed arguments, our design uses a nested layer. This was intended to make it easier for users 
   to allow hierarchical lookup instead of flat key collisions. It also preserves the command hierarchy and subcommand structure

Each subcommand will produce its own `Namespace`, which is then nested inside the parent `Namespace`.

This is similar to a parsed abstract syntax tree.

Example:
```Java
public static Map<String, Object> example(String arguments) throws RuntimeException {
    CommandParser parse = new CommandParser("dispatch");

    var staticType = parse.addSubCommand("static", "type");
    staticType.addArgument("value").asInteger();

    var dynamicType = parse.addSubCommand("dynamic", "type");
    dynamicType.addArgument("value").asString();

    var namespace = parse.parseArgs(arguments);

    String type = namespace.get("type", String.class);
    Namespace sub = namespace.get(type, Namespace.class);

    Object value = sub.get("value", Object.class);
}
```
Output:
```terminaloutput
dispatch static 10
{type="static", static={value=10}, dynamic={}}

dispatch dynamic 10
{type="dynamic", dynamic={value="10"}, static={}}
```
Only active subcommands will have meaningful values. Other subcommands will be empty.

#### 2. Separate Short-Flag Presence Defaults
In argparse4j, setConst is used to represent setting a default when a flag is provided without a value. However, setConst is slightly confusing as it does not seem to fully 
relate the named argument and not all developers will understand how to use unless documentation is read thoroughly. Therefore, our design decided to use a more verbose function 
call `setFlagPresentDefault` which clarifies that this is the value when a flag is used but no specific value is provided. This does not require too much of a look into the 
documentation and the developer can understand what the purpose of the function is for.
```Java
public static Map<String, Object> example(String arguments) throws RuntimeException {
    CommandParser parse = new CommandParser("foo");
     parse.addArgument("--dynamic", "-d")
             .asInteger()
             .setDefault(30)
             .setFlagPresentDefault(10);
}
```
Output:
```terminaloutput
foo -d 20
{dynamic=20}

foo -d
{dynamic=10}

foo
{dynamic=30}
```

#### 3. Positional Argument Order Enforcement
When subcommands are used, a developer can no longer add more posiitonals to the parent command as it is difficult to differentiate what belongs the parent or subcommand.

Example:
```Java
public static Map<String, Object> example(String arguments) throws RuntimeException {
    CommandParser parse = new CommandParser("foo");
    parse.addArgument("value").asInteger();
    var boo = parse.addSubCommand("boo", "type");
    boo.addArgument("index").asString();
    
    // Will cause an exception to be thrown when command is used
    parse.addArgument("next").asInteger();
}
```