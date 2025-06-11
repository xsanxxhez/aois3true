package com.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {

    private final ExpressionParser parser = new ExpressionParser();

    @Test
    void testTokenize_SimpleExpression() {
        String input = "a & b";
        List<String> tokens = parser.tokenize(input);
        assertEquals(List.of("a", "&", "b"), tokens);
    }

    @Test
    void testTokenize_WithImplicationAndNegation() {
        String input = "a -> ~b";
        List<String> tokens = parser.tokenize(input);
        assertEquals(List.of("a", "->", "~", "b"), tokens);
    }

    @Test
    void testTokenize_WithParentheses() {
        String input = "(a|b)&c";
        List<String> tokens = parser.tokenize(input);
        assertEquals(List.of("(", "a", "|", "b", ")", "&", "c"), tokens);
    }

    @Test
    void testTokenize_IgnoresUnknownCharacters() {
        String input = "a # b";
        List<String> tokens = parser.tokenize(input);
        assertEquals(List.of("a", "b"), tokens); // '#' is ignored
    }

    @Test
    void testExtractVariables_Simple() {
        List<String> tokens = List.of("a", "&", "b", "|", "a");
        List<String> variables = parser.extractVariables(tokens);
        assertEquals(List.of("a", "b"), variables); // sorted and unique
    }

    @Test
    void testExtractVariables_NoVariables() {
        List<String> tokens = List.of("!", "&", "|", "->", "~");
        List<String> variables = parser.extractVariables(tokens);
        assertTrue(variables.isEmpty());
    }

    @Test
    void testToPostfix_BasicExpression() {
        List<String> tokens = List.of("a", "&", "b");
        List<String> postfix = parser.toPostfix(tokens);
        assertEquals(List.of("a", "b", "&"), postfix);
    }

    @Test
    void testToPostfix_WithParentheses() {
        List<String> tokens = List.of("(", "a", "|", "b", ")", "&", "c");
        List<String> postfix = parser.toPostfix(tokens);
        assertEquals(List.of("a", "b", "|", "c", "&"), postfix);
    }

    @Test
    void testToPostfix_WithAllOperators() {
        List<String> tokens = List.of("a", "->", "b", "&", "~", "c");
        List<String> postfix = parser.toPostfix(tokens);
        // ~ has higher precedence than &, and & has higher than ->
        assertEquals(List.of("a", "b", "&", "->", "c", "~"), postfix);
    }

    @Test
    void testToPostfix_Complex() {
        List<String> tokens = parser.tokenize("a -> (b & ~c) | d");
        List<String> postfix = parser.toPostfix(tokens);
        assertEquals(List.of("a", "b", "&", "c", "~", "d", "|", "->"), postfix);
    }

    @Test
    void testToPostfix_EmptyInput() {
        List<String> postfix = parser.toPostfix(List.of());
        assertTrue(postfix.isEmpty());
    }
}
