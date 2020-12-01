package ca.jonathanfritz.aoc2020.day1;

import ca.jonathanfritz.aoc2020.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    public int solve(List<Integer> input) {
        for (int i = 0; i < input.size(); i++) {
            final int num1 = input.get(i);
            for (int j = i; j < input.size(); j++) {
                final int num2 = input.get(j);
                if (num1 + num2 == 2020) {
                    return num1 * num2;
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        final List<Integer> input = Utils.loadFromFile("day1.txt")
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
