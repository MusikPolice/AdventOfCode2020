package ca.jonathanfritz.aoc2020.day15;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Part2 {

    // key is number, value is the turns on which it was spoken
    final Map<Long, List<Long>> numbers = new HashMap<>();

    private long solve(List<String> input) {
        final List<Long> startingNumbers = Arrays.stream(input.get(0).split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // the last number that was said
        long lastNumber = 0;

        for (int turn = 1; turn <= 30000000; turn++) {
            if (turn <= startingNumbers.size()) {
                // initialize starting numbers
                final long number = startingNumbers.get(turn - 1);
                lastNumber = countNumber(number, turn);
            } else {
                // if the last number that was said has only been said once, next number is zero
                if (numbers.get(lastNumber).size() == 1) {
                    lastNumber = countNumber(0, turn);
                } else {
                    // otherwise, its the difference between the previous turn and the last time that number was spoken
                    final List<Long> turns = numbers.get(lastNumber);
                    long diff = turns.get(turns.size() - 1) - turns.get(turns.size() - 2);
                    lastNumber = countNumber(diff, turn);
                }
            }
        }

        return lastNumber;
    }

    private long countNumber(long number, long turn) {
        if (numbers.containsKey(number)) {
            List<Long> existing = numbers.get(number);
            final List<Long> turns = Arrays.asList(existing.get(existing.size() - 1), turn);
            numbers.put(number, turns);
        } else {
            numbers.put(number, Collections.singletonList(turn));
        }
        return number;
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day15.txt")
                .collect(Collectors.toList());

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
