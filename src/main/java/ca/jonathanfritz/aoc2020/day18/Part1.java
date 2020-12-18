package ca.jonathanfritz.aoc2020.day18;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<String> input) {
        return input.stream()
                .map(this::solve)
                .mapToLong(value -> value)
                .sum();
    }

    private long solve(String input) {
        final Stack<Expression> stack = new Stack<>();
        Long result = null;
        Operator operator = null;

        for (String s : Utils.splitIntoChars(input)) {
            if (s.trim().length() == 0) {
                continue;
            } if ("+".equals(s)) {
                operator = Operator.ADDITION;
            } else if ("*".equals(s)) {
                operator = Operator.MULTIPLICATION;
            } else if ("(".equals(s)) {
                // if something has already been computed, push it onto the stack
                // an expression like ((2 + 4) * 5) + 1 will push an Expression with a null operator/operand onto the stack
                stack.push(new Expression(result, operator));
                result = null;
                operator = null;
            } else if (")".equals(s)) {
                // if there is a computed result from earlier, pop it off of stack
                // an Expression with a null operand or a null operator indicates ((, so don't try to evaluate it
                final Expression old = stack.pop();
                if (old.operand != null && old.operator != null) {
                    result = evaluate(old.operand, old.operator, result);
                }
            } else {
                // found the next operand
                if (result == null) {
                    result = Long.parseLong(s);
                } else {
                    result = evaluate(Long.parseLong(s), operator, result);
                }
            }
        }

        System.out.println(input + " = " + result);

        return result;
    }

    private long evaluate(long a, Operator operator, long b) {
        switch (operator) {
            case ADDITION:
                return a + b;
            case MULTIPLICATION:
                return a * b;
        }
        throw new UnsupportedOperationException("Unsupported operator " + operator.name());
    }

    private static class Expression {
        private final Long operand;
        private final Operator operator;

        private Expression(Long operand, Operator operator) {
            this.operand = operand;
            this.operator = operator;
        }
    }

    private enum Operator {
        ADDITION,
        MULTIPLICATION;
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day18.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
