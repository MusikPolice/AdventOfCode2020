package ca.jonathanfritz.aoc2020.day25;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<String> input) {
        // our input is the two public keys
        final long cardPublicKey = Long.parseLong(input.get(0));
        final long doorPublicKey = Long.parseLong(input.get(1));

        // we can brute force the loop size for the card
        final long cardLoopSize = guessLoopSize(cardPublicKey);

        // and then use that, along with the known public key for the door to produce the encryption key
        return transform(doorPublicKey, cardLoopSize);
    }

    private long guessLoopSize(long knownPublicKey) {
        final long subjectNumber = 7;
        long loopSize = 0;
        long guessedPublicKey = 1;
        while (guessedPublicKey != knownPublicKey) {
            loopSize++;

            // don't use transform(...) here b/c it repeats all of the calculations for lower loop numbers
            guessedPublicKey = (guessedPublicKey * subjectNumber) % 20201227;
        }
        return loopSize;
    }

    private long transform(long subjectNumber, long loopSize) {
        long value = 1;
        for (int i = 0; i < loopSize; i++) {
            value = value * subjectNumber;
            value = value % 20201227;
        }

        return value;
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day25.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
