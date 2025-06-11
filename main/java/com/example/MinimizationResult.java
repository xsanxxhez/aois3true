package com.example;

import java.util.List;

class MinimizationResult {
    List<LogicTerm> primeImplicants;
    List<String> steps;
    String result;

    public MinimizationResult(List<LogicTerm> primeImplicants, List<String> steps, String result) {
        this.primeImplicants = primeImplicants;
        this.steps = steps;
        this.result = result;
    }
}
