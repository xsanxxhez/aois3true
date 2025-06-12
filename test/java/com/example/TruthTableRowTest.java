package com.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TruthTableRowTest {

    @Test
    void testConstructorAndFields() {
        List<Integer> inputs = List.of(1, 0, 1);
        int result = 1;

        TruthTableRow row = new TruthTableRow(inputs, result);

        assertEquals(inputs, row.inputs);
        assertEquals(result, row.result);
    }
}
