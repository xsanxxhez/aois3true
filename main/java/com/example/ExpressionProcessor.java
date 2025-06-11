package com.example;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class ExpressionProcessor {
    public ExpressionParser parser = new ExpressionParser();
    public TruthTableGenerator tableGenerator = new TruthTableGenerator();
    public FormMinimizer minimizer = new FormMinimizer();
    public ResultDisplayer displayer = new ResultDisplayer();

    public void start() {
        System.out.println("=== LOGIC EXPRESSION MINIMIZER ===");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter logical expression: ");
        String expression = scanner.nextLine();

        try {
            processExpression(expression);
        } catch (Exception e) {
            System.out.println("\nERROR: " + e.getMessage());
        } finally {
            System.out.println("\n=== PROGRAM FINISHED ===");
            scanner.close();
        }
    }

    public void processExpression(String expression) {
        // Parse and validate input
        List<String> tokens = parser.tokenize(expression);
        List<String> variables = parser.extractVariables(tokens);
        Collections.sort(variables);

        if (variables.size() > 3) {
            System.out.println("\nWarning: Karnaugh maps are only supported for up to 3 variables");
        }
        if (variables.isEmpty()) {
            System.out.println("Error: No variables found in expression");
            return;
        }

        // Generate truth table
        List<String> postfix = parser.toPostfix(tokens);
        List<TruthTableRow> truthTable = tableGenerator.generate(postfix, variables);

        // Display truth table
        displayer.showTruthTable(truthTable, variables, expression);

        // Check for trivial cases
        if (checkTrivialCases(truthTable)) {
            return;
        }

        // Create canonical forms
        List<LogicTerm> minterms = minimizer.createTerms(truthTable, variables, true);
        List<LogicTerm> maxterms = minimizer.createTerms(truthTable, variables, false);

        // Original forms
        String sdnfInitial = minterms.isEmpty() ? "0" : minimizer.mergeTerms(minterms, true);
        String sknfInitial = maxterms.isEmpty() ? "1" : minimizer.mergeTerms(maxterms, false);
        displayer.showOriginalForms(sdnfInitial, sknfInitial);

        // SDNF Minimization
        MinimizationResult sdnfResult = minimizer.minimize(minterms, true);
        displayer.showMinimization("SDNF MINIMIZATION (CALCULATION)", sdnfResult);

        // SKNF Minimization
        MinimizationResult sknfResult = minimizer.minimize(maxterms, false);
        displayer.showMinimization("SKNF MINIMIZATION (CALCULATION)", sknfResult);

        // SDNF Table Method
        TableMinimizationResult sdnfTableResult = minimizer.minimizeWithTable(minterms, true);
        displayer.showTableMinimization("SDNF TABLE METHOD", sdnfTableResult);

        // SKNF Table Method
        TableMinimizationResult sknfTableResult = minimizer.minimizeWithTable(maxterms, false);
        displayer.showTableMinimization("SKNF TABLE METHOD", sknfTableResult);

        // SDNF Karnaugh Map
        KMapResult sdnfKMapResult = minimizer.minimizeWithKMap(minterms, true, variables);
        displayer.showKMap("SDNF KARNAUGH MAP", sdnfKMapResult);

        // SKNF Karnaugh Map
        KMapResult sknfKMapResult = minimizer.minimizeWithKMap(maxterms, false, variables);
        displayer.showKMap("SKNF KARNAUGH MAP", sknfKMapResult);
    }

    private boolean checkTrivialCases(List<TruthTableRow> table) {
        boolean allZeros = table.stream().allMatch(row -> row.result == 0);
        boolean allOnes = table.stream().allMatch(row -> row.result == 1);

        if (allZeros) {
            System.out.println("\nThe function is always FALSE (0)");
            return true;
        } else if (allOnes) {
            System.out.println("\nThe function is always TRUE (1)");
            return true;
        }
        return false;
    }
}
