package com.example;

import java.util.List;

class KMapResult {
    MinimizationResult minimization;
    List<String> kmapDisplay;

    public KMapResult(MinimizationResult minimization, List<String> kmapDisplay) {
        this.minimization = minimization;
        this.kmapDisplay = kmapDisplay;
    }
}
