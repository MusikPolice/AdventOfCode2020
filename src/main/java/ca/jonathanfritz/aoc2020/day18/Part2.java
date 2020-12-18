package ca.jonathanfritz.aoc2020.day18;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<String> input) {
        return input.stream()
                .map(this::solve)
                .mapToLong(value -> value)
                .sum();
    }

    private long solve(String input) {
        // parse the input into an ordered list of symbols
        final List<Symbol> symbols = Utils.splitIntoChars(input).stream()
                .filter(s -> s.trim().length() > 0)
                .map(s -> {
                    if ("+".equals(s)) {
                        return new Operator.Addition();
                    } else if ("*".equals(s)) {
                        return new Operator.Multiplication();
                    } else if ("(".equals(s)) {
                        return new Operator.StartParentheses();
                    } else if (")".equals(s)) {
                        return new Operator.EndParentheses();
                    } else {
                        return new Operand(Long.parseLong(s));
                    }
                })
                .collect(Collectors.toList());

        // evaluate all expressions inside of parenthesis first
        while (symbols.stream().anyMatch(s -> s instanceof Operator.StartParentheses)) {
            int start = -1;
            for (int i = 0; i < symbols.size(); i++) {
                if (symbols.get(i) instanceof Operator.StartParentheses) {
                    start = i;
                } else if (symbols.get(i) instanceof Operator.EndParentheses) {
                    // remove everything between ( and )
                    final List<Symbol> subExpression = new ArrayList<>();
                    for (int j = start; j <= i; j++) {
                        final Symbol s = symbols.remove(start);
                        if (!(s instanceof Operator.StartParentheses || s instanceof Operator.EndParentheses)) {
                            subExpression.add(s);
                        }
                    }

                    // evaluate the sub expression and put it back into the parent expression
                    long result = evaluate(subExpression);
                    symbols.add(start, new Operand(result));

                    // start looking back at the beginning
                    break;
                }
            }
        }

        return evaluate(symbols);
    }

    // assumes that subExpression does not contain any parentheses
    private long evaluate(List<Symbol> symbols) {
        while (symbols.stream().anyMatch(s -> s instanceof Operator.Addition)) {
            for (int i = 0; i < symbols.size(); i++) {
                if (symbols.get(i) instanceof Operator.Addition) {
                    // found addition - add the thing before it to the thing after it
                    long a = ((Operand) symbols.get(i - 1)).value;
                    long b = ((Operand) symbols.get(i + 1)).value;
                    long result = a + b;

                    // remove the two operands and the operator, put the result back in their place
                    for (int j = 0; j < 3; j++) {
                        symbols.remove(i - 1);
                    }
                    symbols.add(i - 1, new Operand(result));

                    // start looking back at the beginning
                    break;
                }
            }
        }

        // only thing left is multiplication
        return symbols.stream()
                .filter(s -> s instanceof Operand)
                .map(symbol -> ((Operand) symbol).value)
                .reduce(1L, (a, b) -> a * b);
    }

    private interface Symbol {}

    private static abstract class Operator implements Symbol {

        private static class Multiplication extends Operator {}
        private static class Addition extends Operator {}
        private static class StartParentheses extends Operator {}
        private static class EndParentheses extends Operator {}

    }

    private static class Operand implements Symbol {
        private final Long value;

        private Operand(Long value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day18.txt")
                .collect(Collectors.toList());

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
