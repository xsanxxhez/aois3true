package com.example;

import java.util.*;

class TruthTableGenerator {
    public List<TruthTableRow> generate(List<String> postfix, List<String> variables) {
        List<TruthTableRow> table = new ArrayList<>();
        int combinations = 1 << variables.size();

        for (int i = 0; i < combinations; i++) {
            List<Integer> currentCombo = new ArrayList<>();
            for (int j = 0; j < variables.size(); j++) {
                int shiftAmount = variables.size() - j - 1;
                int bitVal = (i >> shiftAmount) & 1;
                currentCombo.add(bitVal);
            }

            Map<String, Integer> valueMap = new HashMap<>();
            for (int k = 0; k < variables.size(); k++) {
                valueMap.put(variables.get(k), currentCombo.get(k));
            }

            int result = evaluate(postfix, valueMap);
            table.add(new TruthTableRow(currentCombo, result));
        }

        return table;
    }

    private int evaluate(List<String> postfix, Map<String, Integer> valueMap) {
        Stack<Integer> stack = new Stack<>();
        for (String token : postfix) {
            if (valueMap.containsKey(token)) {
                stack.push(valueMap.get(token));
            } else if (token.equals("!")) {
                int top = stack.pop();
                stack.push(1 - top);
            } else {
                int right = stack.pop();
                int left = stack.pop();
                stack.push(applyOperator(token, left, right));
            }
        }
        return stack.pop();
    }

    private int applyOperator(String op, int x, int y) {
        switch (op) {
            case "&":
                return x & y;
            case "|":
                return x | y;
            case "->":
                return (x == 1 && y == 0) ? 0 : 1;
            case "~":
                return x == y ? 1 : 0;
            default:
                return 0;
        }
    }
}
