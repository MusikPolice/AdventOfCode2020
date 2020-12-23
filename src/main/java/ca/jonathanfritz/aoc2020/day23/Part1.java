package ca.jonathanfritz.aoc2020.day23;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private String solve(List<String> input) {
        final List<Integer> cups = Utils.splitIntoChars(input.get(0)).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        final int smallestCupLabel = cups.stream().min(Integer::compareTo).get();
        final int largestCupLabel = cups.stream().max(Integer::compareTo).get();

        int currentCupLabel = cups.get(0);
        for (int move = 0; move < 100; move++) {
            System.out.printf("%n-- move %d --%n", move + 1);
            System.out.printf("cups: %s%n", printCups(cups.indexOf(currentCupLabel), cups));

            final List<Integer> removed = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                removed.add(cups.remove((cups.indexOf(currentCupLabel) + 1) % cups.size()));
            }
            System.out.printf("pick up: %s%n", removed.stream().map(String::valueOf).collect(Collectors.joining(", ")));

            int destinationCupLabel = cups.get(cups.indexOf(currentCupLabel)) - 1;
            do {
                if (destinationCupLabel < smallestCupLabel) {
                    destinationCupLabel = largestCupLabel;
                }
                if (removed.contains(destinationCupLabel)) {
                    destinationCupLabel--;
                }
            } while (!cups.contains(destinationCupLabel));
            System.out.printf("destination: %d%n", destinationCupLabel);

            final int destinationCupIndex = cups.indexOf(destinationCupLabel);
            for (int i = 1; i <= 3; i++) {
                cups.add(destinationCupIndex + i, removed.get(i - 1));
            }

            final int currentCupIndex = (cups.indexOf(currentCupLabel) + 1) % cups.size();
            currentCupLabel = cups.get(currentCupIndex);
        }

        System.out.printf("%n-- final --%n");
        System.out.printf("cups: %s%n", printCups(cups.indexOf(currentCupLabel), cups));

        final StringBuilder sb = new StringBuilder();
        final int startIndex = cups.indexOf(1);
        for (int i = 1; i < cups.size(); i++) {
            sb.append(cups.get((startIndex + i) % cups.size()));
        }
        return sb.toString();
    }

    private String printCups(int currentCupIndex, List<Integer> cups) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cups.size(); i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (i == currentCupIndex) {
                sb.append("(");
            }
            sb.append(cups.get(i));
            if (i == currentCupIndex) {
                sb.append(")");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day23.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
