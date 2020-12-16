package ca.jonathanfritz.aoc2020.day16;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<String> input) {
        final List<Rule> rules = new ArrayList<>();
        final List<Ticket> tickets = new ArrayList<>();
        parseInput(input, rules, tickets);

        final Ticket myTicket = tickets.get(0);

        // discard invalid tickets
        final List<Ticket> validTickets = tickets.stream()
                .filter(t -> t.isValid(rules))
                .collect(Collectors.toList());
        if (validTickets.get(0) != myTicket) {
            throw new RuntimeException("My ticket was invalid!");
        }

        // determine order of fields on valid tickets
        // key is ticket column index, value is list of rules that match all valid tickets for that column
        final Map<Integer, List<Rule>> potentialColumnRuleMatches = new HashMap<>();
        for (int column = 0; column < myTicket.values.size(); column++) {
            int finalColumn = column;
            potentialColumnRuleMatches.put(finalColumn, rules.stream()
                    .filter(rule -> validTickets.stream()
                            .allMatch(t -> rule.isValid(t.values.get(finalColumn))))
                    .collect(Collectors.toList()));
        }
        if (potentialColumnRuleMatches.keySet().size() != myTicket.values.size()) {
            throw new RuntimeException("At least one ticket column did not match any rules");
        }

        // now it's a process of elimination - find the column with only 1 valid rule
        final Map<Integer, Rule> columnRules = new HashMap<>();
        while (true) {
            final int numColumnRules = columnRules.size();
            potentialColumnRuleMatches.entrySet().stream()
                    .filter(entry -> entry.getValue().size() == 1)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(column -> {
                        // remove the column from the map of potential matches
                        final Rule rule = potentialColumnRuleMatches.remove(column).get(0);

                        // remove the rule from other potential column matches
                        potentialColumnRuleMatches.values().forEach(potentialColumnRules -> {
                            if (potentialColumnRules.size() > 1) {
                                potentialColumnRules.remove(rule);
                            }
                        });

                        // record the column rule match
                        columnRules.put(column, rule);

                    });

            if (columnRules.size() == numColumnRules) {
                // we didn't find a new rule match
                break;
            }
        }
        if (columnRules.size() != myTicket.values.size()) {
            throw new RuntimeException("Failed to find a single valid rule for each ticket column");
        }

        // find the column indices whose matching rules start with the word departure
        final List<Integer> columnsToSum = columnRules.entrySet().stream()
                .filter(entry -> entry.getValue().name.startsWith("departure"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // multiply those column values from my ticket together
        long product = 1;
        for (int column : columnsToSum) {
            product *= myTicket.values.get(column);
        }
        return product;
    }

    private void parseInput(List<String> input, List<Rule> rules, List<Ticket> tickets) {
        int section = 0;
        for (String line : input) {
            if (line.trim().length() == 0) {
                section++;
                continue;
            }

            if (section == 0) {
                // parse the rule
                rules.add(new Rule(line));
            } else if (section == 1) {
                if ("your ticket:".equalsIgnoreCase(line.trim())) {
                    continue;
                }
                tickets.add(new Ticket(line));
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

        public boolean isValid(List<Rule> rules) {
            // all values must match at least one rule
            return values.stream()
                    .allMatch(v -> rules.stream()
                            .anyMatch(r -> r.isValid(v)));
        }
    }

    private static class Rule {
        private final String name;
        private final List<Range> ranges = new ArrayList<>();

        public Rule(String line) {
            final String[] components = line.split(":");
            this.name = components[0].trim();

            final String[] tokens = components[1].trim().split(" ");
            for (String token : tokens) {
                if ("or".equalsIgnoreCase(token.trim())) {
                    continue;
                }
                ranges.add(new Range(token));
            }
        }

        public boolean isValid(Integer v) {
            return ranges.stream().anyMatch(r -> r.isValid(v));
        }
    }

    private static class Range {
        private final int min;
        private final int max;

        private Range(String string) {
            final String[] values = string.trim().split("-");
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

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
