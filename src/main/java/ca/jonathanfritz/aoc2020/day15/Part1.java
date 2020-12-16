package ca.jonathanfritz.aoc2020.day15;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part1 {

    // key is number, value is the turns on which it was spoken
    final Map<Integer, List<Integer>> numbers = new HashMap<>();

    private int solve(List<String> input) {
        final List<Integer> startingNumbers = Arrays.stream(input.get(0).split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // the last number that was said
        int lastNumber = 0;

        for (int turn = 1; turn <= 2020; turn++) {
            if (turn <= startingNumbers.size()) {
                final int number = startingNumbers.get(turn - 1);
                lastNumber = countNumber(number, turn);
            } else {
                // if the last number that was said has only been said once, next number is zero
                if (numbers.get(lastNumber).size() == 1) {
                    lastNumber = countNumber(0, turn);
                } else {
                    // otherwise, its the difference between the previous turn and the last time that number was spoken
                    final List<Integer> turns = numbers.get(lastNumber);
                    int diff = turns.get(turns.size() - 1) - turns.get(turns.size() - 2);
                    lastNumber = countNumber(diff, turn);
                }
            }
        }

        return lastNumber;
    }

    private int countNumber(int number, int turn) {
        if (numbers.containsKey(number)) {
            final List<Integer> turns = new ArrayList<>(numbers.get(number));
            turns.add(turn);
            numbers.put(number, turns);
        } else {
            numbers.put(number, Collections.singletonList(turn));
        }
        return number;
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day15.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
