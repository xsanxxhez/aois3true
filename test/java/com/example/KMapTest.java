package com.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KMapTest {



    @Test
    void testMintermInitialization() {
        KMapDimensions dims = new KMapDimensions(2, 2, 1, 1);
        KMap kmap = new KMap(dims, true); // Все значения должны быть 0

        List<String> formatted = kmap.format();
        assertEquals(3, formatted.size());
        assertTrue(formatted.get(1).contains("0\t0"));
        assertTrue(formatted.get(2).contains("0\t0"));
    }

    @Test
    void testMaxtermInitialization() {
        KMapDimensions dims = new KMapDimensions(2, 2, 1, 1);
        KMap kmap = new KMap(dims, false); // Все значения должны быть 1

        List<String> formatted = kmap.format();
        assertEquals(3, formatted.size());
        assertTrue(formatted.get(1).contains("1\t1"));
        assertTrue(formatted.get(2).contains("1\t1"));
    }

    @Test
    void testSetValueMinterm() {
        KMapDimensions dims = new KMapDimensions(2, 2, 1, 1);
        KMap kmap = new KMap(dims, true);

        kmap.setValue("0", "1");
        List<String> formatted = kmap.format();

        assertEquals("0\t0\t1", formatted.get(1));
    }

    @Test
    void testSetValueMaxterm() {
        KMapDimensions dims = new KMapDimensions(2, 2, 1, 1);
        KMap kmap = new KMap(dims, false);

        kmap.setValue("0", "1");
        List<String> formatted = kmap.format();

        assertEquals("0\t1\t0", formatted.get(1));
    }

    @Test
    void testGrayCodeCorrectness() {
        KMapDimensions dims = new KMapDimensions(4, 4, 2, 2);
        KMap kmap = new KMap(dims, true);

        // Gray codes for 2 bits: 00, 01, 11, 10
        List<String> formatted = kmap.format();
        assertFalse(formatted.get(0).contains("00"));
        assertFalse(formatted.get(0).contains("01"));
        assertFalse(formatted.get(0).contains("11"));
        assertFalse(formatted.get(0).contains("10"));
        assertFalse(formatted.get(1).startsWith("00"));
    }
}
