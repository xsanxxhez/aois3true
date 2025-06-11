package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KMap {
    private final KMapDimensions dimensions;
    private final boolean isMinterm;
    private final int[][] map;
    private final List<String> rowCodes;
    private final List<String> colCodes;

    public KMap(KMapDimensions dimensions, boolean isMinterm) {
        this.dimensions = dimensions;
        this.isMinterm = isMinterm;
        this.map = new int[dimensions.rows][dimensions.cols];

        // Initialize with default values
        for (int i = 0; i < dimensions.rows; i++) {
            for (int j = 0; j < dimensions.cols; j++) {
                map[i][j] = isMinterm ? 0 : 1;
            }
        }

        // Generate Gray codes
        this.rowCodes = generateGrayCode(dimensions.rowVars);
        this.colCodes = generateGrayCode(dimensions.colVars);
    }

    public void setValue(String rowBits, String colBits) {
        try {
            int rowIdx = dimensions.rowVars > 0 ? rowCodes.indexOf(rowBits) : 0;
            int colIdx = colCodes.indexOf(colBits);
            map[rowIdx][colIdx] = isMinterm ? 1 : 0;
        } catch (Exception e) {
            // Ignore invalid positions
        }
    }

    public List<String> format() {
        List<String> display = new ArrayList<>();

        // Create header
        StringBuilder header = new StringBuilder();
        if (dimensions.rowVars > 0) {
            header.append("\\");
        }
        for (String code : colCodes) {
            header.append("\t").append(code);
        }
        display.add(header.toString());

        // Add rows
        for (int i = 0; i < dimensions.rows; i++) {
            StringBuilder row = new StringBuilder();
            if (dimensions.rowVars > 0 && i < rowCodes.size()) {
                row.append(rowCodes.get(i));
            }
            for (int j = 0; j < dimensions.cols; j++) {
                row.append("\t").append(map[i][j]);
            }
            display.add(row.toString());
        }

        return display;
    }

    private List<String> generateGrayCode(int bits) {
        if (bits == 0) return Collections.emptyList();

        List<String> grayCodes = new ArrayList<>();
        grayCodes.add("0");
        grayCodes.add("1");

        for (int i = 1; i < bits; i++) {
            List<String> reflected = new ArrayList<>(grayCodes);
            Collections.reverse(reflected);

            for (String code : grayCodes) {
                code = "0" + code;
            }
            for (String code : reflected) {
                code = "1" + code;
            }

            grayCodes.addAll(reflected);
        }

        return grayCodes.subList(0, 1 << bits);
    }
}
