package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

class ExpressionParser {
    public List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        int position = 0;
        while (position < expression.length()) {
            char ch = expression.charAt(position);
            if (ch == ' ') {
                position++;
            } else if (isVariable(ch)) {
                tokens.add(String.valueOf(ch));
                position++;
            } else if (ch == '-' && position + 1 < expression.length() && expression.charAt(position + 1) == '>') {
                tokens.add("->");
                position += 2;
            } else if (ch == '~') {
                tokens.add("~");
                position++;
            } else if ("!&|()".indexOf(ch) != -1) {
                tokens.add(String.valueOf(ch));
                position++;
            } else {
                position++;
            }
        }
        return tokens;
    }

    public List<String> extractVariables(List<String> tokens) {
        return tokens.stream()
                .filter(token -> token.length() == 1 && isVariable(token.charAt(0)))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> toPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (isVariable(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty()) stack.pop();
            } else {
                while (!stack.isEmpty() && !stack.peek().equals("(") &&
                        getPriority(stack.peek()) >= getPriority(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private boolean isVariable(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    private boolean isVariable(String token) {
        return token.length() == 1 && isVariable(token.charAt(0));
    }

    private int getPriority(String op) {
        switch (op) {
            case "!":
                return 4;
            case "&":
                return 3;
            case "|":
                return 2;
            case "->":
                return 1;
            case "~":
                return 1;
            default:
                return -1;
        }
    }
}
