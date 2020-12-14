package ca.jonathanfritz.aoc2020.day13;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<String> input) {
        final long desiredDepature = Integer.parseInt(input.get(0));
        final List<Integer> busIds = Arrays.stream(input.get(1).split(","))
                .filter(id -> !id.equalsIgnoreCase("x"))
                .map(s -> Integer.parseInt(s.trim()))
                .collect(Collectors.toList());

        long smallestWaitTime = Integer.MAX_VALUE;
        long nextBusId = 0;
        for (long busId : busIds) {
            long remainder = desiredDepature % busId;
            long prevDeparture = desiredDepature - remainder;
            long nextDeparture = prevDeparture + busId;

            long waitTime = nextDeparture - desiredDepature;
            if (waitTime < smallestWaitTime) {
                nextBusId = busId;
                smallestWaitTime = waitTime;
            }
        }

        return nextBusId * smallestWaitTime;
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day13.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
