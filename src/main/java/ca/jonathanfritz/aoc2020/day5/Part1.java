package ca.jonathanfritz.aoc2020.day5;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        final Map<String, Integer> boardingPassSeatIds = input.stream()
                .collect(Collectors.toMap(
                        boardingPass -> boardingPass,
                        boardingPass -> {
                            final String rowPortion = boardingPass.substring(0, 7);
                            final String colPortion = boardingPass.substring(7);

                            final int row = partition(rowPortion, 127);
                            final int col = partition(colPortion, 7);
                            int seatId = (row * 8) + col;
                            System.out.printf("%s: row %d, col %d, seatId %d%n", boardingPass, row, col, seatId);
                            return seatId;
                        }));

        return boardingPassSeatIds.values().stream()
                .max(Integer::compareTo)
                .orElseThrow();
    }

    private int partition(String boardingPass, int upperBound) {
        Range range = new Range(0, upperBound);
        for (char indicator : boardingPass.toCharArray()) {
            final Direction d = Direction.fromChar(indicator);
            range = range.halve(d);
        }
        return range.lowerBound;
    }

    private enum Direction {
        upper('B','R'),
        lower('F','L');

        private final Set<Character> indicators = new HashSet<>();

        Direction(char ...indicators) {
            for (char c : indicators) {
                this.indicators.add(c);
            }
        }

        static Direction fromChar(char indicator) {
            return Arrays.stream(Direction.values())
                    .filter(d -> d.indicators.contains(indicator))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid indicator " + indicator + " supplied"));
        }
    }

    private static class Range {
        private final int lowerBound;
        private final int upperBound;

        Range(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        Range halve(Direction d) {
            final double half = (upperBound - lowerBound) / 2.0;
            if (d == Direction.upper) {
                return new Range(lowerBound + (int)Math.ceil(half), upperBound);
            } else {
                return new Range(lowerBound, lowerBound + (int)Math.floor(half));
            }
        }
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day5.txt").collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println("Max seat id: " + part1.solve(input));
    }
}
