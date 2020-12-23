package ca.jonathanfritz.aoc2020.day23;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private String solve(List<String> input) {
        // input is a single string of integers, each character representing a cup with a label between 1 and 9 inclusive
        final List<Integer> cups = Utils.splitIntoChars(input.get(0)).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        final int smallestCupLabel = cups.stream().min(Integer::compareTo).get();
        final int largestCupLabel = cups.stream().max(Integer::compareTo).get();

        // the crab does 100 moves, starting with the leftmost cup in the line
        int currentCupLabel = cups.get(0);
        for (int move = 0; move < 100; move++) {
            System.out.printf("%n-- move %d --%n", move + 1);
            System.out.printf("cups: %s%n", printCups(cups.indexOf(currentCupLabel), cups));

            // The crab picks up the three cups that are immediately clockwise of the current cup. They are removed from
            // the circle; cup spacing is adjusted as necessary to maintain the circle.
            final List<Integer> removed = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                removed.add(cups.remove((cups.indexOf(currentCupLabel) + 1) % cups.size()));
            }
            System.out.printf("pick up: %s%n", removed.stream().map(String::valueOf).collect(Collectors.joining(", ")));

            // The crab selects a destination cup: the cup with a label equal to the current cup's label minus one.
            int destinationCupLabel = cups.get(cups.indexOf(currentCupLabel)) - 1;
            do {

                // If at any point in this process the value goes below the lowest value on any cup's label, it wraps
                // around to the highest value on any cup's label instead.
                if (destinationCupLabel < smallestCupLabel) {
                    destinationCupLabel = largestCupLabel;
                }

                // If this would select one of the cups that was just picked up, the crab will keep subtracting one
                // until it finds a cup that wasn't just picked up.
                if (removed.contains(destinationCupLabel)) {
                    destinationCupLabel--;
                }
            } while (!cups.contains(destinationCupLabel));
            System.out.printf("destination: %d%n", destinationCupLabel);

            // The crab places the cups it just picked up so that they are immediately clockwise of the destination cup.
            // They keep the same order as when they were picked up.
            final int destinationCupIndex = cups.indexOf(destinationCupLabel);
            for (int i = 1; i <= 3; i++) {
                cups.add(destinationCupIndex + i, removed.get(i - 1));
            }

            // The crab selects a new current cup: the cup which is immediately clockwise of the current cup.
            final int currentCupIndex = (cups.indexOf(currentCupLabel) + 1) % cups.size();
            currentCupLabel = cups.get(currentCupIndex);
        }

        System.out.printf("%n-- final --%n");
        System.out.printf("cups: %s%n", printCups(cups.indexOf(currentCupLabel), cups));

        // Starting after the cup labeled 1, collect the other cups' labels clockwise into a single string with no
        // extra characters
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
