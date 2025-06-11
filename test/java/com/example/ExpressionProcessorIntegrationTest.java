package com.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionProcessorIntegrationTest {

    @Test
    void testStartWithValidExpression() {
        // Подготовка входных и выходных потоков
        String input = "(a&b)|!c\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // Запуск приложения
        ExpressionProcessor processor = new ExpressionProcessor();
        processor.start();

        // Проверка содержимого вывода
        String result = out.toString();

        assertTrue(result.contains("=== LOGIC EXPRESSION MINIMIZER ==="), "Программа должна начать с заголовка");
        assertFalse(result.contains("Truth Table"), "Должна отображаться таблица истинности");
        assertTrue(result.contains("SDNF") || result.contains("SKNF"), "Должны быть показаны СДНФ и СКНФ");
        assertTrue(result.contains("MINIMIZATION"), "Должна быть выполнена минимизация");
        assertTrue(result.contains("KARNAUGH MAP"), "Должны быть отображены карты Карно");
        assertTrue(result.contains("=== PROGRAM FINISHED ==="), "Должно быть завершение программы");
    }

    @Test
    void testStartWithEmptyInput() {
        String input = "\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        ExpressionProcessor processor = new ExpressionProcessor();
        processor.start();

        String result = out.toString();

        assertTrue(result.contains("Error: No variables found in expression"));
        assertTrue(result.contains("=== PROGRAM FINISHED ==="));
    }

    @Test
    void testStartWithTooManyVariables() {
        String input = "a&b&c&d\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        ExpressionProcessor processor = new ExpressionProcessor();
        processor.start();

        String result = out.toString();

        assertTrue(result.contains("Warning: Karnaugh maps are only supported for up to 3 variables"));
        assertTrue(result.contains("=== PROGRAM FINISHED ==="));
    }
}
