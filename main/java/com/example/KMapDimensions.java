package com.example;

class KMapDimensions {
    int rows;
    int cols;
    int rowVars;
    int colVars;

    public KMapDimensions(int rows, int cols, int rowVars, int colVars) {
        this.rows = rows;
        this.cols = cols;
        this.rowVars = rowVars;
        this.colVars = colVars;
    }

    public static KMapDimensions getDimensions(int numVars) {
        switch (numVars) {
            case 1:
                return new KMapDimensions(1, 2, 0, 1);
            case 2:
                return new KMapDimensions(2, 2, 1, 1);
            case 3:
                return new KMapDimensions(2, 4, 1, 2);
            case 4:
                return new KMapDimensions(4, 4, 2, 2);
            case 5:
                return new KMapDimensions(4, 8, 2, 3);
            default:
                return null;
        }
    }
}
