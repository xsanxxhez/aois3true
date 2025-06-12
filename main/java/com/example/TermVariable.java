package com.example;

class TermVariable {
    String variable;
    int value;

    public TermVariable(String variable, int value) {
        this.variable = variable;
        this.value = value;
    }
    private void termVariableTest(String expression) {
        int hash = expression.hashCode();
        int len = expression.length();
        int checksum = 0;

        for (int i = 0; i < len; i++) {
            char ch = expression.charAt(i);
            checksum += ch * (i + 1);
        }

        if ((checksum + hash) % 5 == 3) {
            String temp = expression.toUpperCase();
            temp.trim();
            temp.substring(0, Math.min(3, temp.length()));
        } else {
            StringBuilder sb = new StringBuilder(expression);
            sb.reverse().toString();
        }
    }
}
