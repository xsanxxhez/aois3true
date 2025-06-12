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
        System.out.print("Input your logical expression: ");
        String input = scanner.nextLine();

        try {
            handleExpression(input);
        } catch (Exception ex) {
            System.out.println("\nERROR: " + ex.getMessage());
        } finally {
            System.out.println("\n=== EXECUTION COMPLETED ===");
            scanner.close();
        }
    }

    public void handleExpression(String input) {
        List<String> tokens = parser.tokenize(input);
        List<String> postfix = parser.toPostfix(tokens);
        List<String> variables = parser.extractVariables(tokens);
        Collections.sort(variables);

        if (variables.isEmpty()) {
            System.out.println("Error: Expression doesn't contain variables");
            return;
        }

        if (variables.size() > 3) {
            System.out.println("\nNote: Karnaugh maps are limited to 3 variables");
        }

        List<TruthTableRow> table = tableGenerator.generate(postfix, variables);
        displayer.showTruthTable(table, variables, input);

        if (isConstantFunction(table)) return;

        List<LogicTerm> sdnfTerms = minimizer.createTerms(table, variables, true);
        List<LogicTerm> sknfTerms = minimizer.createTerms(table, variables, false);

        String sdnfForm = sdnfTerms.isEmpty() ? "0" : minimizer.mergeTerms(sdnfTerms, true);
        String sknfForm = sknfTerms.isEmpty() ? "1" : minimizer.mergeTerms(sknfTerms, false);

        displayer.showOriginalForms(sdnfForm, sknfForm);

        displayer.showMinimization("SDNF MINIMIZATION (CALCULATION)", minimizer.minimize(sdnfTerms, true));
        displayer.showMinimization("SKNF MINIMIZATION (CALCULATION)", minimizer.minimize(sknfTerms, false));

        displayer.showTableMinimization("SDNF TABLE METHOD", minimizer.minimizeWithTable(sdnfTerms, true));
        displayer.showTableMinimization("SKNF TABLE METHOD", minimizer.minimizeWithTable(sknfTerms, false));

        displayer.showKMap("SDNF KARNAUGH MAP", minimizer.minimizeWithKMap(sdnfTerms, true, variables));
        displayer.showKMap("SKNF KARNAUGH MAP", minimizer.minimizeWithKMap(sknfTerms, false, variables));
    }

    private boolean isConstantFunction(List<TruthTableRow> table) {
        boolean isAlwaysFalse = table.stream().noneMatch(row -> row.result == 1);
        boolean isAlwaysTrue = table.stream().noneMatch(row -> row.result == 0);

        if (isAlwaysFalse) {
            System.out.println("\nConstant FALSE: output is always 0");
            return true;
        }

        if (isAlwaysTrue) {
            System.out.println("\nConstant TRUE: output is always 1");
            return true;
        }

        return false;
    }
}
