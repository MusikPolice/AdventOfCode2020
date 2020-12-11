package ca.jonathanfritz.aoc2020.day10;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<Long> input) {
        // max volt diff is 3 and we must use every list element, so a sorted list makes sense
        input.sort(Long::compareTo);

        long currentVoltage = 0;
        int oneVoltDiffCount = 0;
        int threeVoltDiffCount = 1; // device is always 3 volts higher than highest adaptor
        for (long rating : input) {
            if (rating >= currentVoltage + 1 && rating <= currentVoltage + 3) {
                if (rating - currentVoltage == 1) {
                    oneVoltDiffCount++;
                } else if (rating - currentVoltage == 3) {
                    threeVoltDiffCount++;
                }
                currentVoltage = rating;
            } else {
                throw new RuntimeException(String.format("%d volt adaptor can't be added to a chain with effective voltage of %d", rating, currentVoltage));
            }
        }

        return oneVoltDiffCount * threeVoltDiffCount;
    }

    public static void main (String[] args) {
        final List<Long> input = Utils.loadFromFile("day10.txt")
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
