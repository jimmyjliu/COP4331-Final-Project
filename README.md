# COP4331 Final Project
CLI Arg parsing framework for Java

## Prerequisites
- Java 17+
- Gradle

## Beginner's Guide: How to Use the Library
The command system handles command structures and multi-argument parsing. The argument system handles parsing a single `String` input value into typed data.

The usual workflow is: make a command, add arguments, parse the user input, then pull typed values out of the resulting `Namespace`.

This is meant to get someone started quickly. It does not try to explain every design choice or edge case whereas the Feature Showcase below is better for that.

### Basic setup
```Java
import oop.project.library.command.Command;
import oop.project.library.command.Namespace;
```

Create a command with the name of the program or subprogram you are modeling. `parseArgs(...)` gets the argument string, meaning the part after the command name.

```Java
Command search = new Command("search");
```

### Positional arguments
Positional arguments are matched in order of declaration, first come first served.

```Java
Command add = new Command("add");
add.addArgument("left").asInteger();
add.addArgument("right").asInteger();

Namespace args = add.parseArgs("8 12");

Integer left = args.get("left", Integer.class);
Integer right = args.get("right", Integer.class);
```

Here, `left` is `8` and `right` is `12`.

Arguments support method chaining for parsing and validation, similar to argparse4j:

```Java
Command fizzbuzz = new Command("fizzbuzz");
fizzbuzz.addArgument("number")
        .asInteger()
        .range(1, 100);

Namespace args = fizzbuzz.parseArgs("21");
Integer number = args.get("number", Integer.class);
```

### Named arguments
Named arguments use flag notation. Long flags take a value from the next token.

```Java
Command resize = new Command("resize");
resize.addArgument("--width").asInteger();
resize.addArgument("--height").asInteger();

Namespace args = resize.parseArgs("--width 2560 --height 1664");

Integer width = args.get("width", Integer.class);
Integer height = args.get("height", Integer.class);
```

The name used with `Namespace.get(...)` is the flag name without the dashes.

Named arguments can also have aliases. If a named argument has multiple long and/or short flags, the first flag provided when the argument is created is the name you use later:

```Java
Command search = new Command("search");
search.addArgument("term").asString();
search.addArgument("--case-insensitive", "-i")
      .asBoolean()
      .setDefault(false)
      .setFlagPresentDefault(true);

Namespace args = search.parseArgs("README -i");

String term = args.get("term", String.class);
Boolean caseInsensitive = args.get("case-insensitive", Boolean.class);
```

In this example, `term` is `"README"` and `caseInsensitive` is `true`.

### Useful argument types
The built-in argument types cover the common cases. Each one converts the raw string value and then runs any validation you added.

```Java
Command profile = new Command("profile");

profile.addArgument("username")
       .asString()
       .regex("[a-zA-Z0-9_]+");

profile.addArgument("--age")
       .asInteger()
       .range(13, 120);

profile.addArgument("--score")
       .asDouble()
       .range(0.0, 100.0);

profile.addArgument("--active")
       .asBoolean();

Namespace args = profile.parseArgs("LBJ23 --age 41 --score 97.5 --active true");
```

After parsing:

```Java
String username = args.get("username", String.class);
Integer age = args.get("age", Integer.class);
Double score = args.get("score", Double.class);
Boolean active = args.get("active", Boolean.class);
```

For strings, `.choices(...)` is useful when only a few values are valid:

```Java
Command difficulty = new Command("difficulty");
difficulty.addArgument("level")
          .asString()
          .choices("peaceful", "easy", "normal", "hard");

Namespace args = difficulty.parseArgs("normal");
String level = args.get("level", String.class);
```

Enums are supported too:

```Java
enum Difficulty {
    PEACEFUL, EASY, NORMAL, HARD
}

Command game = new Command("game");
game.addArgument("difficulty")
    .asEnum(Difficulty.class)
    .caseSensitive(false);

Namespace args = game.parseArgs("hard");
Difficulty difficulty = args.get("difficulty", Difficulty.class);
```

Custom types are supported with `as(...)` and a custom parsing function:

```Java
import java.time.LocalDate;

Command schedule = new Command("schedule");
schedule.addArgument("date")
        .as(LocalDate.class)
        .parser(LocalDate::parse);

Namespace args = schedule.parseArgs("2026-04-30");
LocalDate date = args.get("date", LocalDate.class);
```

### Defaults
Defaults are applied for non-present positional and named arguments.

```Java
Command echo = new Command("echo");
echo.addArgument("message")
    .asString()
    .setDefault("echo, echo, echo...");

Namespace args = echo.parseArgs("");
String message = args.get("message", String.class);
```

Named flags can also have a separate flag-present default, which is the value used when the flag is present but no specific value is provided. This is most useful for boolean-style short flags:

```Java
Command grep = new Command("grep");
grep.addArgument("term").asString();
grep.addArgument("--ignore-case", "-i")
    .asBoolean()
    .setDefault(false)
    .setFlagPresentDefault(true);

Namespace args = grep.parseArgs("hello -i");

String term = args.get("term", String.class);
Boolean ignoreCase = args.get("ignore-case", Boolean.class);
```

### Subcommands
Subcommands let one command branch into smaller commands. Subcommands are stored as a nested `Namespace` inside the parent `Namespace`.

```Java
Command dispatch = new Command("dispatch");

Command staticType = dispatch.addSubCommand("static", "type");
staticType.addArgument("value").asInteger();

Command dynamicType = dispatch.addSubCommand("dynamic", "type");
dynamicType.addArgument("value").asString();

Namespace args = dispatch.parseArgs("static 10");

String type = args.get("type", String.class);
Namespace subcommandArgs = args.get(type, Namespace.class);
Integer value = subcommandArgs.get("value", Integer.class);
```

Here, `type` is `"static"` and `value` is `10`.

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
Command parser = new Command("search");

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
    Command parse = new Command("dispatch");

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
    Command parse = new Command("foo");
     parse.addArgument("--dynamic", "-d")
             .asInteger()
             .setDefault(30)
             .setFlagPresentDefault(10);
}
```
Output:
```terminaloutput
foo --dynamic 20
{dynamic=20}

foo -d
{dynamic=10}

foo
{dynamic=30}
```

#### 3. Positional Argument Order Enforcement
When subcommands are used, a developer can no longer add more positionals to the parent command as it is difficult to differentiate what belongs to the parent or subcommand.

Example:
```Java
public static Map<String, Object> example(String arguments) throws RuntimeException {
    Command parse = new Command("foo");
    parse.addArgument("value").asInteger();
    var boo = parse.addSubCommand("boo", "type");
    boo.addArgument("index").asString();
    
    // Will cause an exception to be thrown during command configuration
    parse.addArgument("next").asInteger();
}
```
