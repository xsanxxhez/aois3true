package com.example;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FormMinimizerTest {
    private final FormMinimizer minimizer = new FormMinimizer();

    @Test
    void testCreateTermsMinterm() {
        List<TruthTableRow> table = Arrays.asList(
                new TruthTableRow(Arrays.asList(0, 0), 0),
                new TruthTableRow(Arrays.asList(0, 1), 1),
                new TruthTableRow(Arrays.asList(1, 0), 1),
                new TruthTableRow(Arrays.asList(1, 1), 0)
        );
        List<String> variables = Arrays.asList("a", "b");

        List<LogicTerm> terms = minimizer.createTerms(table, variables, true);

        assertEquals(2, terms.size());
        assertEquals("a=0, b=1", formatTermVariables(terms.get(0)));
        assertEquals("a=1, b=0", formatTermVariables(terms.get(1)));
    }

    @Test
    void testCreateTermsMaxterm() {
        List<TruthTableRow> table = Arrays.asList(
                new TruthTableRow(Arrays.asList(0, 0), 0),
                new TruthTableRow(Arrays.asList(0, 1), 1),
                new TruthTableRow(Arrays.asList(1, 0), 1),
                new TruthTableRow(Arrays.asList(1, 1), 0)
        );
        List<String> variables = Arrays.asList("a", "b");

        List<LogicTerm> terms = minimizer.createTerms(table, variables, false);

        assertEquals(2, terms.size());
        assertEquals("a=0, b=0", formatTermVariables(terms.get(0)));
        assertEquals("a=1, b=1", formatTermVariables(terms.get(1)));
    }

    @Test
    void testMergeTermsMinterm() {
        List<LogicTerm> terms = Arrays.asList(
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 0))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 0), new TermVariable("b", 1)))
        );

        String result = minimizer.mergeTerms(terms, true);
        assertEquals("a!b ∨ !ab", result);
    }

    @Test
    void testMergeTermsMaxterm() {
        List<LogicTerm> terms = Arrays.asList(
                new LogicTerm(Arrays.asList(new TermVariable("a", 0), new TermVariable("b", 0))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 1)))
        );

        String result = minimizer.mergeTerms(terms, false);
        assertEquals("(a|b) ∧ (!a|!b)", result);
    }

    @Test
    void testMinimizeSimpleMinterm() {
        List<LogicTerm> terms = Arrays.asList(
                new LogicTerm(Arrays.asList(new TermVariable("a", 0), new TermVariable("b", 1))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 0))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 1)))
        );

        MinimizationResult result = minimizer.minimize(terms, true);
        assertEquals("b ∨ a", result.result);
        assertFalse(result.steps.isEmpty());
    }

    @Test
    void testMinimizeSimpleMaxterm() {
        List<LogicTerm> terms = Arrays.asList(
                new LogicTerm(Arrays.asList(new TermVariable("a", 0), new TermVariable("b", 0))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 0), new TermVariable("b", 1))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 0)))
        );

        MinimizationResult result = minimizer.minimize(terms, false);
        assertEquals("(a) ∧ (b)", result.result);
        assertFalse(result.steps.isEmpty());
    }

    @Test
    void testMinimizeWithTable() {
        List<LogicTerm> terms = Arrays.asList(
                new LogicTerm(Arrays.asList(new TermVariable("a", 0), new TermVariable("b", 1))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 0))),
                new LogicTerm(Arrays.asList(new TermVariable("a", 1), new TermVariable("b", 1)))
        );

        TableMinimizationResult result = minimizer.minimizeWithTable(terms, true);
        assertNotNull(result.table);
        assertEquals(4, result.table.size()); // header + 3 terms
        assertEquals("b ∨ a", result.minimization.result);
    }

    @Test
    void testCanCombineTerms() {
        LogicTerm term1 = new LogicTerm(Arrays.asList(
                new TermVariable("a", 0),
                new TermVariable("b", 1),
                new TermVariable("c", 0))
        );

        LogicTerm term2 = new LogicTerm(Arrays.asList(
                new TermVariable("a", 0),
                new TermVariable("b", 1),
                new TermVariable("c", 1))
        );

        TermCombinationResult result = minimizer.canCombine(term1, term2);
        assertTrue(result.canCombine);
        assertEquals("c", result.diffVar);
    }

    @Test
    void testCombineTerms() {
        LogicTerm term = new LogicTerm(Arrays.asList(
                new TermVariable("a", 0),
                new TermVariable("b", 1),
                new TermVariable("c", 0))
        );

        LogicTerm combined = minimizer.combineTerms(term, "c");
        assertEquals(2, combined.variables.size());
        assertEquals("a=0, b=1", formatTermVariables(combined));
    }

    @Test
    void testIsCovered() {
        LogicTerm implicant = new LogicTerm(Arrays.asList(
                new TermVariable("a", 1))
        );

        LogicTerm term = new LogicTerm(Arrays.asList(
                new TermVariable("a", 1),
                new TermVariable("b", 0))
        );

        assertTrue(minimizer.isCovered(implicant, term));
    }

    private String formatTermVariables(LogicTerm term) {
        return term.variables.stream()
                .map(tv -> tv.variable + "=" + tv.value)
                .collect(Collectors.joining(", "));
    }
}