package ca.jonathanfritz.aoc2020.day9;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part2 {

    private long solve(int preambleLength, List<Long> input) {
        // find the first number in the list that is not the sum of some pair of numbers
        // in the previous preambleLength numbers
        final long target = findTargetNumber(preambleLength, input);

        // now we need to find some range of numbers in input that sum to target
        int left = 0;
        int right = 1;
        while (left < right && right < input.size()) {
            // sum together all input values between left and right
            final long sum = IntStream.range(left, right)
                    .mapToObj(input::get)
                    .reduce(Long::sum)
                    .get();

            if (sum == target) {
                // found a range that sums to target - return sum of the smallest and largest numbers in our range
                final long min = IntStream.range(left, right)
                        .mapToObj(input::get)
                        .min(Comparator.naturalOrder())
                        .get();
                final long max = IntStream.range(left, right)
                        .mapToObj(input::get)
                        .max(Comparator.naturalOrder())
                        .get();
                return min + max;
            } else if (sum < target) {
                // sum isn't big enough yet - add another number to the range
                right++;
            } else {
                // sum is too big - drop some smaller values out of the range
                left++;
            }
        }

        // this is bad
        return 0;
    }

    private long findTargetNumber(int preambleLength, List<Long> input) {
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
        final Part2 part2 = new Part2();
        System.out.println("Sum of low and high values is " + part2.solve(preambleLength, input));
    }
}
