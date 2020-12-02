package ca.jonathanfritz.aoc2020.day2;

import ca.jonathanfritz.aoc2020.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part2 {

    private int solve(List<String> input) {
        return input.stream()
                .map(s -> {
                    final String policy = s.split(":")[0].trim();
                    final String range = policy.split(" ")[0].trim();

                    // input char index is 1-based
                    int pos1 = Integer.parseInt(range.split("-")[0]) - 1;
                    int pos2 = Integer.parseInt(range.split("-")[1]) - 1;

                    final char character = policy.split(" ")[1].trim().charAt(0);
                    final String password = s.split(":")[1].trim();

                    // xor - exactly one of the two characters must match
                    return (password.charAt(pos1) == character || password.charAt(pos2) == character) &&
                            !(password.charAt(pos1) == character && password.charAt(pos2) == character)
                            ? 1 : 0;
                })
                .mapToInt(Integer::intValue)
                .sum();
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day2.txt")
                .collect(Collectors.toList());
        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));
    }
}
