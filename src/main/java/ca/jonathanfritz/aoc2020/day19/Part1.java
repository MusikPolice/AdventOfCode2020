package ca.jonathanfritz.aoc2020.day19;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private void tests() {
        /*// single string match
        long result = solve(new ArrayList<>(Arrays.asList(
                "0: 1",
                "1: \"a\"",
                "",
                "a",
                "b"
        )));
        if (result != 1) {
            throw new RuntimeException("String match test fails");
        }

        // or match
        result = solve(new ArrayList<>(Arrays.asList(
                "0: 1 | 2",
                "1: \"a\"",
                "2: \"b\"",
                "",
                "a",
                "b",
                "c"
        )));
        if (result != 2) {
            throw new RuntimeException("Or match test fails");
        }

        // and match
        result = solve(new ArrayList<>(Arrays.asList(
                "0: 1 2",
                "1: \"a\"",
                "2: \"b\"",
                "",
                "ab",
                "ba"
        )));
        if (result != 1) {
            throw new RuntimeException("And match test fails");
        }

        // complex or match
        result = solve(new ArrayList<>(Arrays.asList(
                "0: 1 2 | 2 1",
                "1: \"a\"",
                "2: \"b\"",
                "",
                "ab",
                "ba",
                "aa"
        )));
        if (result != 2) {
            throw new RuntimeException("Complex or match test fails");
        }*/

        // or nested within and match
        final long result = solve(new ArrayList<>(Arrays.asList(
                "0: 1 2",
                "1: \"a\"",
                "2: 1 | 1 1",
                "",
                "aa",
                "aaa",
                "a",
                "aaaa"
        )));
        if (result != 2) {
            throw new RuntimeException("Or nested within and match test fails");
        }
    }

    private long solve(List<String> input) {
        final List<Rule> rules = new ArrayList<>();
        while (true) {
            final String line = input.remove(0);

            // rules and signals are separated by a line break
            if (line.trim().length() == 0) {
                break;
            }

            final String rule = line.split(":")[1].trim();
            if (rule.contains("|")) {
                final List<AndRule> andRules = Arrays.stream(rule.split("\\|"))
                        .map(s -> Arrays.stream(s.trim().split(" "))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()))
                        .map(AndRule::new)
                        .collect(Collectors.toList());
                rules.add(new OrRule(andRules));
            } else if (rule.contains("\"")) {
                rules.add(new StringRule(rule.replace("\"", "")));
            } else {
                rules.add(
                        new AndRule(Arrays.stream(rule.trim().split(" "))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList())
                        )
                );
            }
        }

        // each line of input must exactly match rule 0
        long sum = 0;
        for (String line : input) {
            final List<String> tokens = Utils.splitIntoChars(line);
            final boolean ruleMatches = rules.get(0).match(tokens, rules);
            if (ruleMatches && tokens.isEmpty()) {
                System.out.printf("%s matches%n", line);
                sum++;
            } else {
                System.out.printf("%s does not match%n", line);
            }
        }
        return sum;
    }

    private interface Rule {
        boolean match(List<String> tokens, List<Rule> rules);
    }

    private static class AndRule implements Rule {
        private final List<Integer> andRules;

        private AndRule(List<Integer> andRules) {
            this.andRules = andRules;
        }

        @Override
        public boolean match(List<String> tokens, List<Rule> rules) {
            // iterate over other rules that must match and evaluate each
            for (Rule rule : andRules.stream().map(rules::get).collect(Collectors.toList())) {
                if (!rule.match(tokens, rules)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String toString() {
            return "AndRule{" +
                    "andRules=" + andRules +
                    '}';
        }
    }

    private static class OrRule implements Rule {
        private final List<AndRule> orRules;

        private OrRule(List<AndRule> orRules) {
            this.orRules = orRules;
        }

        @Override
        public boolean match(List<String> tokens, List<Rule> rules) {
            for (AndRule rule : orRules) {
                final List<String> copyOfTokens = new ArrayList<>(tokens);
                if (rule.match(copyOfTokens, rules)) {
                    // rule passed, remove from the start of tokens until it is same size as copy of tokens
                    // this applies the same change to the parent list as was applied to the copy by the passing rule
                    // if the rule didn't pass, the parent list was not modified
                    while (tokens.size() > copyOfTokens.size()) {
                        tokens.remove(0);
                    }

                    // TODO: There's a problem here - if both parts of the or expression match, but one consumes more
                    //       tokens than the other, which one should affect the parent list of tokens? Should we always
                    //       be greedy and take the most tokens? Or maybe we should be conservative? Or branch both?
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "OrRule{" +
                    "orRules=" + orRules +
                    '}';
        }
    }

    private static class StringRule implements Rule {
        private final String match;

        private StringRule(String match) {
            this.match = match;
        }

        @Override
        public boolean match(List<String> tokens, List<Rule> rules) {
            if (tokens.isEmpty()) {
                // can't possibly match
                return false;
            }

            // check if the n elements starting at offset of subject match this rule
            if (match.equals(tokens.remove(0))) {
                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            return "StringRule{" +
                    "match='" + match + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day19.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        part1.tests();
        //System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
