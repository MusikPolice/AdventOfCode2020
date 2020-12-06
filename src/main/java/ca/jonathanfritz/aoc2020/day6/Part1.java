package ca.jonathanfritz.aoc2020.day6;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<String> input) {
        final List<Long> counts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            final String line = input.get(i).trim();
            sb.append(line.trim());

            final boolean isLastLine = i == input.size() - 1;
            if (line.length() == 0 || isLastLine) {
                // split line into characters, take distinct count of answers
                counts.add(sb.toString().chars().mapToObj(c -> (char) c).distinct().count());
                sb = new StringBuilder();
            }
        }

        return counts.stream().mapToLong(count -> count).sum();
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day6.txt").collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println("Sum of counts: " + part1.solve(input));
    }
}
