package ca.jonathanfritz.aoc2020.day2;

import ca.jonathanfritz.aoc2020.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        return input.stream()
                .map(s -> {
                    final String policy = s.split(":")[0];
                    final String range = policy.split(" ")[0];
                    int lowerBound = Integer.parseInt(range.split("-")[0]);
                    int upperBound = Integer.parseInt(range.split("-")[1]);
                    final char character = policy.split(" ")[1].charAt(0);
                    final String password = s.split(":")[1];

                    int count = 0;
                    for (int i = 0; i < password.length(); i++) {
                        if (password.charAt(i) == character) {
                            count++;
                        }
                    }

                    return count >= lowerBound && count <= upperBound ? 1 : 0;
                })
                .mapToInt(Integer::intValue)
                .sum();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        final List<String> input = Utils.loadFromFile("day2.txt")
                .collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
