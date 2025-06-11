package com.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResultDisplayerTest {

    @Test
    void testShowTruthTableOutputsCorrectly() {
        ResultDisplayer displayer = new ResultDisplayer();

        List<TruthTableRow> rows = List.of(
                new TruthTableRow(List.of(0, 0), 0),
                new TruthTableRow(List.of(0, 1), 1),
                new TruthTableRow(List.of(1, 0), 1),
                new TruthTableRow(List.of(1, 1), 0)
        );

        List<String> vars = List.of("A", "B");
        String expr = "A XOR B";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        displayer.showTruthTable(rows, vars, expr);

        String output = outContent.toString();
        assertTrue(output.contains("ТАБЛИЦА ИСТИННОСТИ"));
        assertTrue(output.contains("A"));
        assertTrue(output.contains("B"));
        assertTrue(output.contains("A XOR B"));
        assertTrue(output.contains("│  0  │  0  │  0   │"));
    }
}
