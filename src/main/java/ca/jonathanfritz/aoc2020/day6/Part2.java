package ca.jonathanfritz.aoc2020.day6;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<String> input) {
        final List<Long> counts = new ArrayList<>();

        final List<String> groupLines = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            final String line = input.get(i).trim();
            if (line.length() > 0) {
                groupLines.add(line);
            }

            final boolean isLastLine = i == input.size() - 1;
            if (line.length() == 0 || isLastLine) {
                counts.add(
                    // append all group answers into a single string, split into characters
                    String.join("", groupLines).chars()
                        .mapToObj(c -> (char) c)
                        .map(String::valueOf)

                        // collect chars into map, counting num times each character appears
                        .collect(Collectors.toMap(s -> s, s -> 1, Integer::sum))
                        .values().stream()

                        // count characters that appear once per line, i.e. questions that each group member answered "yes"
                        .filter(count -> count >= groupLines.size())
                        .count());

                groupLines.clear();
            }
        }

        return counts.stream().mapToLong(count -> count).sum();
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day6.txt").collect(Collectors.toList());
        final Part2 part2 = new Part2();
        System.out.println("Sum of counts: " + part2.solve(input));
    }
}
