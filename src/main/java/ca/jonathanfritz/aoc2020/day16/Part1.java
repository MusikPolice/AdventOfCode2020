package ca.jonathanfritz.aoc2020.day16;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        final List<Rule> rules = new ArrayList<>();
        final List<Ticket> tickets = new ArrayList<>();
        parseInput(input, rules, tickets);

        final List<Integer> invalidFields = new ArrayList<>();
        for (Ticket t : tickets) {
            invalidFields.addAll(t.getInvalidFields(rules));
        }

        return invalidFields.stream()
                .mapToInt(value -> value)
                .sum();
    }

    private void parseInput(List<String> input, List<Rule> rules, List<Ticket> tickets) {
        int section = 0;
        for (String line : input) {
            if (line.trim().length() == 0) {
                section++;
                continue;
            }

            if (section == 0) {
                // discard the rule name
                final String components = line.split(":")[1].trim();

                // parsing ranges into rules
                final String[] tokens = components.split(" ");
                rules.add(new Rule(tokens[0]));
                rules.add(new Rule(tokens[2]));
            } else if (section == 1) {
                // do nothing - ignore my ticket for now
                continue;
            } else if (section == 2) {
                if ("nearby tickets:".equalsIgnoreCase(line.trim())) {
                    // ignore the header
                    continue;
                }
                tickets.add(new Ticket(line));
            }
        }
    }

    private static class Ticket {
        final List<Integer> values;

        public Ticket(String line) {
            values = Arrays.stream(line.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        public List<Integer> getInvalidFields(List<Rule> rules) {
            return values.stream()
                    .filter(v -> rules.stream().noneMatch(r -> r.isValid(v)))
                    .collect(Collectors.toList());
        }
    }

    private static class Rule {
        private final int min;
        private final int max;

        public Rule(String token) {
            final String[] values = token.split("-");
            this.min = Integer.parseInt(values[0]);
            this.max = Integer.parseInt(values[1]);
        }

        public boolean isValid(Integer v) {
            return v >= this.min && v <= this.max;
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day16.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
