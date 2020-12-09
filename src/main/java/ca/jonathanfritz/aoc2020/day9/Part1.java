package ca.jonathanfritz.aoc2020.day9;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part1 {

    private long solve(int preambleLength, List<Long> input) {
        // initialize our window with the first preambleLength numbers
        final List<Long> numbers = input.stream()
                .limit(preambleLength)
                .collect(Collectors.toList());

        int offset = preambleLength;
        while (offset < input.size()) {
            // consider the next input
            final long nextNumber = input.get(offset);
            System.out.printf("%nNext number is %d:%n", nextNumber);

            // check to see if it is the sum of two numbers in our window
            // generates all possible pairs of numbers, excluding pairs where both numbers are the same
            // checks to see if any pair exists such that the sum of both numbers equals nextNumber
            final Optional<Pair<Long>> sum = numbers.stream()
                    .map(i -> numbers.stream()
                            .filter(j -> !i.equals(j))
                            .map(j -> new Pair<>(i, j)))
                    .flatMap((Function<Stream<Pair<Long>>, Stream<Pair<Long>>>) pairStream -> pairStream)
                    .peek(pair -> System.out.println(pair.toString()))
                    .filter(pair -> pair.left + pair.right == nextNumber)
                    .findFirst();

            if (sum.isPresent()) {
                System.out.printf("%d + %d = %d%n", sum.get().left, sum.get().right, nextNumber);
            } else {
                return nextNumber;
            }

            // advance the window
            numbers.remove(0);
            numbers.add(numbers.size(), nextNumber);
            offset++;
        }

        return 0;
    }

    private static class Pair<T> {
        private final T left;
        private final T right;

        private Pair(T left, T right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "[" + left + "," + right + "]";
        }
    }

    public static void main (String[] args) {
        final List<Long> input = Utils.loadFromFile("day9.txt")
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
        final int preambleLength = 25;
        final Part1 part1 = new Part1();
        System.out.println("First number that is not the sum of two of the " + preambleLength + " numbers before it: " + part1.solve(preambleLength, input));
    }
}
