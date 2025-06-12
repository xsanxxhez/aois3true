package com.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TruthTableGeneratorTest {

    @Test
    void testSingleVariable() {
        TruthTableGenerator generator = new TruthTableGenerator();
        List<String> postfix = List.of("A");
        List<String> variables = List.of("A");

        List<TruthTableRow> table = generator.generate(postfix, variables);

        assertEquals(2, table.size());
        assertEquals(List.of(0), table.get(0).inputs);
        assertEquals(0, table.get(0).result);
        assertEquals(List.of(1), table.get(1).inputs);
        assertEquals(1, table.get(1).result);
    }

    @Test
    void testNegation() {
        TruthTableGenerator generator = new TruthTableGenerator();
        List<String> postfix = List.of("A", "!");
        List<String> variables = List.of("A");

        List<TruthTableRow> table = generator.generate(postfix, variables);

        assertEquals(2, table.size());
        assertEquals(1, table.get(0).result); // !0 = 1
        assertEquals(0, table.get(1).result); // !1 = 0
    }

    @Test
    void testAndOperation() {
        TruthTableGenerator generator = new TruthTableGenerator();
        List<String> postfix = List.of("A", "B", "&");
        List<String> variables = List.of("A", "B");

        List<TruthTableRow> table = generator.generate(postfix, variables);

        assertEquals(4, table.size());
        assertEquals(0, table.get(0).result); // 0 & 0
        assertEquals(0, table.get(1).result); // 0 & 1
        assertEquals(0, table.get(2).result); // 1 & 0
        assertEquals(1, table.get(3).result); // 1 & 1
    }

    @Test
    void testOrOperation() {
        TruthTableGenerator generator = new TruthTableGenerator();
        List<String> postfix = List.of("A", "B", "|");
        List<String> variables = List.of("A", "B");

        List<TruthTableRow> table = generator.generate(postfix, variables);

        assertEquals(4, table.size());
        assertEquals(0, table.get(0).result); // 0 | 0
        assertEquals(1, table.get(1).result); // 0 | 1
        assertEquals(1, table.get(2).result); // 1 | 0
        assertEquals(1, table.get(3).result); // 1 | 1
    }

    @Test
    void testImplication() {
        TruthTableGenerator generator = new TruthTableGenerator();
        List<String> postfix = List.of("A", "B", "->");
        List<String> variables = List.of("A", "B");

        List<TruthTableRow> table = generator.generate(postfix, variables);

        assertEquals(4, table.size());
        assertEquals(1, table.get(0).result); // 0 -> 0
        assertEquals(1, table.get(1).result); // 0 -> 1
        assertEquals(0, table.get(2).result); // 1 -> 0
        assertEquals(1, table.get(3).result); // 1 -> 1
    }

    @Test
    void testEquivalence() {
        TruthTableGenerator generator = new TruthTableGenerator();
        List<String> postfix = List.of("A", "B", "~");
        List<String> variables = List.of("A", "B");

        List<TruthTableRow> table = generator.generate(postfix, variables);

        assertEquals(4, table.size());
        assertEquals(1, table.get(0).result); // 0 ~ 0
        assertEquals(0, table.get(1).result); // 0 ~ 1
        assertEquals(0, table.get(2).result); // 1 ~ 0
        assertEquals(1, table.get(3).result); // 1 ~ 1
    }
}
