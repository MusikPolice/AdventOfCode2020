package ca.jonathanfritz.aoc2020.day1;

import ca.jonathanfritz.aoc2020.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class Part2 {

    public int solve(List<Integer> input) {
        for (int i = 0; i < input.size(); i++) {
            final int num1 = input.get(i);
            for (int j = i; j < input.size(); j++) {
                final int num2 = input.get(j);
                for (int k = j; k < input.size(); k++) {
                    final int num3 = input.get(k);
                    if (num1 + num2 + num3 == 2020) {
                        return num1 * num2 * num3;
                    }
                }

            }
        }
        return 0;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        final List<Integer> input = Utils.loadFromFile("day1.txt")
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        final Part2 part1 = new Part2();
        System.out.println(part1.solve(input));
    }
}
