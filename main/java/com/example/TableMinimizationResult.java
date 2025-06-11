package com.example;

import java.util.List;

class TableMinimizationResult {
    MinimizationResult minimization;
    List<List<String>> table;

    public TableMinimizationResult(MinimizationResult minimization, List<List<String>> table) {
        this.minimization = minimization;
        this.table = table;
    }
}
