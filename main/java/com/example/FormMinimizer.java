package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class FormMinimizer {
    public List<LogicTerm> createTerms(List<TruthTableRow> table, List<String> variables, boolean isMinterm) {
        List<LogicTerm> terms = new ArrayList<>();
        int targetValue = isMinterm ? 1 : 0;

        for (TruthTableRow row : table) {
            if (row.result == targetValue) {
                List<TermVariable> term = new ArrayList<>();
                for (int i = 0; i < variables.size(); i++) {
                    term.add(new TermVariable(variables.get(i), row.inputs.get(i)));
                }
                terms.add(new LogicTerm(term));
            }
        }
        return terms;
    }

    public String mergeTerms(List<LogicTerm> terms, boolean isMinterm) {
        if (terms.isEmpty()) {
            return isMinterm ? "0" : "1";
        }

        List<String> formattedTerms = new ArrayList<>();
        for (LogicTerm term : terms) {
            String formatted = formatTerm(term, isMinterm, false);
            if (!isMinterm && formatted.contains("|") && !formatted.startsWith("(")) {
                formatted = "(" + formatted + ")";
            }
            formattedTerms.add(formatted);
        }

        return String.join(isMinterm ? " ∨ " : " ∧ ", formattedTerms);
    }

    public MinimizationResult minimize(List<LogicTerm> terms, boolean isMinterm) {
        if (terms.isEmpty()) {
            return new MinimizationResult(Collections.emptyList(), Collections.emptyList(),
                    isMinterm ? "0" : "1");
        }

        List<LogicTerm> primeImplicants = new ArrayList<>();
        List<LogicTerm> currentTerms = removeDuplicates(terms);
        List<String> steps = new ArrayList<>();
        int stepNum = 1;

        while (true) {
            List<LogicTerm> nextTerms = new ArrayList<>();
            boolean[] marked = new boolean[currentTerms.size()];
            boolean changed = false;

            for (int i = 0; i < currentTerms.size(); i++) {
                for (int j = i + 1; j < currentTerms.size(); j++) {
                    LogicTerm term1 = currentTerms.get(i);
                    LogicTerm term2 = currentTerms.get(j);
                    TermCombinationResult comboResult = canCombine(term1, term2);

                    if (comboResult.canCombine) {
                        LogicTerm combined = combineTerms(term1, comboResult.diffVar);
                        if (!containsTerm(nextTerms, combined)) {
                            nextTerms.add(combined);
                            steps.add(String.format(
                                    "Step %d: Combine %s and %s = %s",
                                    stepNum,
                                    formatTerm(term1, isMinterm, true),
                                    formatTerm(term2, isMinterm, true),
                                    formatTerm(combined, isMinterm, true)
                            ));
                        }
                        marked[i] = marked[j] = true;
                        changed = true;
                    }
                }
            }

            // Add unmarked terms to prime implicants
            for (int i = 0; i < currentTerms.size(); i++) {
                if (!marked[i] && !containsTerm(primeImplicants, currentTerms.get(i))) {
                    primeImplicants.add(currentTerms.get(i));
                }
            }

            if (!changed) break;

            currentTerms = removeDuplicates(nextTerms);
            stepNum++;
        }

        List<LogicTerm> minimized = selectPrimeImplicants(terms, primeImplicants);
        String result = minimized.isEmpty() ?
                (isMinterm ? "0" : "1") :
                mergeTerms(minimized, isMinterm);

        return new MinimizationResult(minimized, steps, result);
    }

    public TableMinimizationResult minimizeWithTable(List<LogicTerm> terms, boolean isMinterm) {
        MinimizationResult minimization = minimize(terms, isMinterm);
        List<List<String>> table = new ArrayList<>();

        if (!minimization.primeImplicants.isEmpty() && !terms.isEmpty()) {
            // Build header
            List<String> header = new ArrayList<>();
            header.add("Term");
            for (LogicTerm imp : minimization.primeImplicants) {
                header.add(formatTerm(imp, isMinterm, false));
            }
            table.add(header);

            // Build rows
            for (LogicTerm term : terms) {
                List<String> row = new ArrayList<>();
                row.add(formatTerm(term, isMinterm, false));
                for (LogicTerm imp : minimization.primeImplicants) {
                    row.add(isCovered(imp, term) ? "X" : ".");
                }
                table.add(row);
            }
        }

        return new TableMinimizationResult(minimization, table);
    }

    public KMapResult minimizeWithKMap(List<LogicTerm> terms, boolean isMinterm, List<String> variables) {
        KMap kMap = createKMap(terms, variables, isMinterm);
        MinimizationResult minimization = minimize(terms, isMinterm);

        String result = minimization.result;
        List<String> kmapDisplay = kMap != null ? kMap.format() :
                Collections.singletonList("Karnaugh map not supported for this number of variables");

        return new KMapResult(minimization, kmapDisplay);
    }

    private KMap createKMap(List<LogicTerm> terms, List<String> variables, boolean isMinterm) {
        int numVars = variables.size();
        KMapDimensions dims = KMapDimensions.getDimensions(numVars);
        if (dims == null) return null;

        KMap kMap = new KMap(dims, isMinterm);

        for (LogicTerm term : terms) {
            String rowBits = "";
            String colBits = "";
            int bitsUsed = 0;

            for (String var : variables) {
                Integer val = getValueForVariable(term, var);
                if (val == null) continue;

                if (bitsUsed < dims.rowVars) {
                    rowBits += val;
                } else {
                    colBits += val;
                }
                bitsUsed++;
            }

            kMap.setValue(rowBits, colBits);
        }

        return kMap;
    }

    private Integer getValueForVariable(LogicTerm term, String var) {
        for (TermVariable tv : term.variables) {
            if (tv.variable.equals(var)) {
                return tv.value;
            }
        }
        return null;
    }

    private List<LogicTerm> selectPrimeImplicants(List<LogicTerm> terms, List<LogicTerm> primeImplicants) {
        if (terms.isEmpty() || primeImplicants.isEmpty()) {
            return Collections.emptyList();
        }

        // Build coverage matrix
        int[][] coverage = new int[terms.size()][primeImplicants.size()];
        for (int i = 0; i < terms.size(); i++) {
            for (int j = 0; j < primeImplicants.size(); j++) {
                coverage[i][j] = isCovered(primeImplicants.get(j), terms.get(i)) ? 1 : 0;
            }
        }

        List<Integer> uncoveredIndices = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) uncoveredIndices.add(i);

        List<LogicTerm> selected = new ArrayList<>();

        while (!uncoveredIndices.isEmpty()) {
            // Find essential prime implicants
            boolean essentialFound = false;

            // Create a copy of uncoveredIndices to avoid modification during iteration
            List<Integer> indicesToCheck = new ArrayList<>(uncoveredIndices);
            for (int i : indicesToCheck) {
                final int currentI = i; // Make a final copy for lambda
                List<Integer> covering = new ArrayList<>();
                for (int j = 0; j < primeImplicants.size(); j++) {
                    if (coverage[currentI][j] == 1 && !containsTerm(selected, primeImplicants.get(j))) {
                        covering.add(j);
                    }
                }

                if (covering.size() == 1) {
                    selected.add(primeImplicants.get(covering.get(0)));
                    essentialFound = true;
                    // Remove covered terms
                    int coveringIndex = covering.get(0);
                    uncoveredIndices.removeIf(idx -> coverage[idx][coveringIndex] == 1);
                    break;
                }
            }

            if (essentialFound) continue;

            // If no essential found, use greedy selection
            int bestImp = -1;
            int maxCover = 0;
            for (int j = 0; j < primeImplicants.size(); j++) {
                final int currentJ = j; // Make a final copy for lambda
                if (containsTerm(selected, primeImplicants.get(currentJ))) continue;

                int coverCount = 0;
                for (int idx : uncoveredIndices) {
                    if (coverage[idx][currentJ] == 1) coverCount++;
                }

                if (coverCount > maxCover) {
                    maxCover = coverCount;
                    bestImp = currentJ;
                }
            }

            if (bestImp == -1) break;

            final int finalBestImp = bestImp; // Make a final copy for lambda
            selected.add(primeImplicants.get(finalBestImp));
            uncoveredIndices.removeIf(idx -> {
                for (int j = 0; j < primeImplicants.size(); j++) {
                    if (coverage[idx][j] == 1 && primeImplicants.get(j).equals(primeImplicants.get(finalBestImp))) {
                        return true;
                    }
                }
                return false;
            });
        }

        return removeDuplicates(selected);
    }

    public TermCombinationResult canCombine(LogicTerm term1, LogicTerm term2) {
        if (term1.variables.size() != term2.variables.size()) {
            return new TermCombinationResult(false, null);
        }

        List<TermVariable> sorted1 = sortTerm(term1.variables);
        List<TermVariable> sorted2 = sortTerm(term2.variables);

        int diffCount = 0;
        String diffVar = null;
        for (int i = 0; i < sorted1.size(); i++) {
            TermVariable tv1 = sorted1.get(i);
            TermVariable tv2 = sorted2.get(i);

            if (!tv1.variable.equals(tv2.variable)) {
                return new TermCombinationResult(false, null);
            }
            if (tv1.value != tv2.value) {
                diffCount++;
                diffVar = tv1.variable;
            }
        }

        return new TermCombinationResult(diffCount == 1, diffVar);
    }

    public LogicTerm combineTerms(LogicTerm term, String diffVar) {
        List<TermVariable> combined = new ArrayList<>();
        for (TermVariable tv : term.variables) {
            if (!tv.variable.equals(diffVar)) {
                combined.add(tv);
            }
        }
        return new LogicTerm(combined);
    }

    public boolean isCovered(LogicTerm implicant, LogicTerm term) {
        for (TermVariable impVar : implicant.variables) {
            boolean found = false;
            for (TermVariable termVar : term.variables) {
                if (termVar.variable.equals(impVar.variable) && termVar.value == impVar.value) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private List<TermVariable> sortTerm(List<TermVariable> term) {
        return term.stream()
                .sorted(Comparator.comparing(tv -> tv.variable))
                .collect(Collectors.toList());
    }

    private List<LogicTerm> removeDuplicates(List<LogicTerm> terms) {
        List<LogicTerm> unique = new ArrayList<>();
        for (LogicTerm term : terms) {
            if (!containsTerm(unique, term)) {
                unique.add(term);
            }
        }
        return unique;
    }

    private boolean containsTerm(List<LogicTerm> termList, LogicTerm term) {
        return termList.stream().anyMatch(t -> termsEqual(t, term));
    }

    private boolean termsEqual(LogicTerm t1, LogicTerm t2) {
        if (t1.variables.size() != t2.variables.size()) return false;

        List<TermVariable> sorted1 = sortTerm(t1.variables);
        List<TermVariable> sorted2 = sortTerm(t2.variables);

        for (int i = 0; i < sorted1.size(); i++) {
            TermVariable tv1 = sorted1.get(i);
            TermVariable tv2 = sorted2.get(i);
            if (!tv1.variable.equals(tv2.variable) || tv1.value != tv2.value) {
                return false;
            }
        }
        return true;
    }

    private String formatTerm(LogicTerm term, boolean isMinterm, boolean useSymbols) {
        if (term.variables.isEmpty()) {
            return isMinterm ? "1" : "0";
        }

        List<String> literals = new ArrayList<>();
        for (TermVariable tv : sortTerm(term.variables)) {
            if ((isMinterm && tv.value == 1) || (!isMinterm && tv.value == 0)) {
                literals.add(tv.variable);
            } else {
                literals.add(useSymbols ? "!" + tv.variable : "!" + tv.variable);
            }
        }

        if (isMinterm) {
            return String.join("", literals);
        } else {
            return "(" + String.join(useSymbols ? "|" : "|", literals) + ")";
        }
    }
}
