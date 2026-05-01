package oop.project.library.scenarios;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class FeatureShowcaseScenariosTests {

    @ParameterizedTest
    @MethodSource
    public void testTypedArgumentConstruction(String name, String command, Map<String, Object> expected) {
        test(command, expected);
    }

    private static Stream<Arguments> testTypedArgumentConstruction() {
        return Stream.of(
                Arguments.of("Valid search term", """
                showcase-search hello
                """, Map.of("term", "hello")),
                Arguments.of("Missing search term", """
                showcase-search
                """, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testFlagPresentDefault(String name, String command, Map<String, Object> expected) {
        test(command, expected);
    }

    private static Stream<Arguments> testFlagPresentDefault() {
        return Stream.of(
                Arguments.of("Explicit long flag value", """
                showcase-foo --dynamic 20
                """, Map.of("dynamic", 20)),
                Arguments.of("Present flag default", """
                showcase-foo -d
                """, Map.of("dynamic", 10)),
                Arguments.of("Missing flag default", """
                showcase-foo
                """, Map.of("dynamic", 30))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testNestedSubcommands(String name, String command, Map<String, Object> expected) {
        test(command, expected);
    }

    private static Stream<Arguments> testNestedSubcommands() {
        return Stream.of(
                Arguments.of("Static subcommand", """
                showcase-dispatch static 10
                """, Map.of("type", "static", "value", 10)),
                Arguments.of("Dynamic subcommand", """
                showcase-dispatch dynamic 10
                """, Map.of("type", "dynamic", "value", "10")),
                Arguments.of("Static subcommand invalid integer", """
                showcase-dispatch static ten
                """, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testPositionalOrderEnforcement(String name, String command, Map<String, Object> expected) {
        test(command, expected);
    }

    private static Stream<Arguments> testPositionalOrderEnforcement() {
        return Stream.of(
                Arguments.of("Configuration safety", """
                showcase-foo-order
                """, Map.of("error", "Positional Arguments Must Be Declared Before Adding Any Subcommands."))
        );
    }

    private static void test(String command, Map<String, Object> expected) {
        try {
            var result = Scenarios.parse(command.stripTrailing());
            Assertions.assertEquals(expected, result);
        } catch (RuntimeException e) {
            if (!e.getStackTrace()[0].getClassName().startsWith("oop.project.library.scenarios")) {
                Assertions.fail("Unexpected exception, expected an exception thrown from within a Scenario.", e);
            } else if (expected != null || e instanceof UnsupportedOperationException) {
                Assertions.fail(e.getCause() != null ? e.getCause() : e);
            }
        }
    }
}
