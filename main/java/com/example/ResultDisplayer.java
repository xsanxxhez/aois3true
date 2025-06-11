package com.example;

import java.util.List;

class ResultDisplayer {
    public void showTruthTable(List<TruthTableRow> table, List<String> variables, String expression) {
        System.out.println("\n╔═══════════════════════╗");
        System.out.println("║  ТАБЛИЦА ИСТИННОСТИ   ║");
        System.out.println("╚═══════════════════════╝");

        // Заголовок таблицы
        System.out.print("┌");
        for (int i = 0; i < variables.size(); i++) {
            System.out.print("─────┬");
        }
        System.out.println("──────┐");

        System.out.print("│");
        for (String var : variables) {
            System.out.printf(" %-3s │", var);
        }
        System.out.printf(" %-4s │%n", expression);

        System.out.print("├");
        for (int i = 0; i < variables.size(); i++) {
            System.out.print("─────┼");
        }
        System.out.println("──────┤");

        // Данные таблицы
        for (TruthTableRow row : table) {
            System.out.print("│");
            for (int val : row.inputs) {
                System.out.printf("  %d  │", val);
            }
            System.out.printf("  %d   │%n", row.result);
        }

        System.out.print("└");
        for (int i = 0; i < variables.size(); i++) {
            System.out.print("─────┴");
        }
        System.out.println("──────┘");
    }

    public void showOriginalForms(String sdnf, String sknf) {
        System.out.println("\n════════════════════════════");
        System.out.println("  КАНОНИЧЕСКИЕ ФОРМЫ  ");
        System.out.println("════════════════════════════");
        System.out.println("СДНФ: " + (sdnf.isEmpty() ? "0" : sdnf));
        System.out.println("СКНФ: " + (sknf.isEmpty() ? "1" : sknf));
        System.out.println("════════════════════════════");
    }

    public void showMinimization(String title, MinimizationResult result) {
        System.out.println("\n◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆");
        System.out.println("  " + title.toUpperCase());
        System.out.println("◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆");

        if (result.steps.isEmpty()) {
            System.out.println("Минимизация не требуется - форма уже минимальна");
        } else {
            System.out.println("Этапы минимизации:");
            for (int i = 0; i < result.steps.size(); i++) {
                System.out.printf("%2d. %s%n", i + 1, result.steps.get(i));
            }
        }

        System.out.println("\nМинимизированная форма: " + result.result);
        System.out.println("◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆");
    }

    public void showTableMinimization(String title, TableMinimizationResult result) {
        System.out.println("\n▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄");
        System.out.println("  " + title.toUpperCase());
        System.out.println("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        if (!result.minimization.steps.isEmpty()) {
            System.out.println("Этапы минимизации:");
            for (int i = 0; i < result.minimization.steps.size(); i++) {
                System.out.printf("%2d. %s%n", i + 1, result.minimization.steps.get(i));
            }
        }

        if (result.table != null && !result.table.isEmpty()) {
            System.out.println("\nТаблица покрытия:");
            printFancyTable(result.table);
        } else {
            System.out.println("Таблица покрытия не сгенерирована");
        }

        System.out.println("\nРезультат: " + result.minimization.result);
        System.out.println("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
    }

    public void showKMap(String title, KMapResult result) {
        System.out.println("\n██████████████████████████");
        System.out.println("  " + title.toUpperCase());
        System.out.println("██████████████████████████");

        if (result.kmapDisplay != null) {
            System.out.println("\nКарта Карно:");
            for (String line : result.kmapDisplay) {
                System.out.println("  " + line);
            }
        } else {
            System.out.println("Карта Карно недоступна");
        }

        System.out.println("\nМинимизированная форма: " + result.minimization.result);
        System.out.println("██████████████████████████");
    }

    private void printFancyTable(List<List<String>> table) {
        if (table.isEmpty()) return;

        // Определение ширины столбцов
        int[] colWidths = new int[table.get(0).size()];
        for (List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i).length() > colWidths[i]) {
                    colWidths[i] = row.get(i).length();
                }
            }
        }

        // Верхняя граница таблицы
        printTableBorder(colWidths, "╔", "╦", "╗");

        // Заголовок
        printTableRow(table.get(0), colWidths);
        printTableBorder(colWidths, "╠", "╬", "╣");

        // Данные
        for (int i = 1; i < table.size(); i++) {
            printTableRow(table.get(i), colWidths);
            if (i < table.size() - 1) {
                printTableBorder(colWidths, "╟", "╫", "╢");
            }
        }

        // Нижняя граница
        printTableBorder(colWidths, "╚", "╩", "╝");
    }

    private void printTableRow(List<String> row, int[] colWidths) {
        System.out.print("║");
        for (int i = 0; i < row.size(); i++) {
            System.out.printf(" %-" + colWidths[i] + "s ║", row.get(i));
        }
        System.out.println();
    }

    private void printTableBorder(int[] colWidths, String left, String middle, String right) {
        System.out.print(left);
        for (int i = 0; i < colWidths.length; i++) {
            for (int j = 0; j < colWidths[i] + 2; j++) {
                System.out.print("═");
            }
            if (i < colWidths.length - 1) {
                System.out.print(middle);
            }
        }
        System.out.println(right);
    }
}