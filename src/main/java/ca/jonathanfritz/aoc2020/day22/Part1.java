package ca.jonathanfritz.aoc2020.day22;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<String> input) {
        // player decks are modelled as FIFO queues
        final Queue<Integer> player1 = new LinkedList<>();
        final Queue<Integer> player2 = new LinkedList<>();

        // initialize the decks
        parseInput(input, player1, player2);

        // play until one deck holds all the cards
        int round = 1;
        while (!(player1.isEmpty() || player2.isEmpty())) {
            System.out.printf("-- Round %d --%n", round);
            System.out.printf("Player 1's deck: %s%n", player1.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            System.out.printf("Player 2's deck: %s%n", player2.stream().map(String::valueOf).collect(Collectors.joining(", ")));

            int player1Card = player1.poll();
            int player2Card = player2.poll();
            System.out.printf("Player 1 plays: %d%n", player1Card);
            System.out.printf("Player 2 plays: %d%n", player2Card);

            if (player1Card > player2Card) {
                System.out.println("Player 1 wins the round!");
                player1.add(player1Card);
                player1.add(player2Card);
            } else {
                System.out.println("Player 2 wins the round!");
                player2.add(player2Card);
                player2.add(player1Card);
            }
            System.out.println("");
            round++;
        }

        System.out.println("");
        System.out.printf("== Post-game results ==%n");
        System.out.printf("Player 1's deck: %s%n", player1.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.printf("Player 2's deck: %s%n", player2.stream().map(String::valueOf).collect(Collectors.joining(", ")));

        if (player1.isEmpty()) {
            // player two wins
            return calculateScore(player2);
        } else {
            // player 1 wins
            return calculateScore(player1);
        }
    }

    private long calculateScore(Queue<Integer> deck) {
        final List<Integer> reversedDeck = new ArrayList<>(deck);
        Collections.reverse(reversedDeck);

        long score = 0;
        for (int i = 0; i < reversedDeck.size(); i++) {
            score += reversedDeck.get(i) * (i + 1);
        }
        return score;
    }

    private void parseInput(List<String> input, Queue<Integer> player1, Queue<Integer> player2) {
        int player = 1;
        for (String line : input) {
            if (line.startsWith("Player")) {
                continue;
            } else if (line.trim().length() == 0) {
                player = 2;
                continue;
            }

            int card = Integer.parseInt(line);
            if (player == 1) {
                player1.add(card);
            } else {
                player2. add(card);
            }
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day22.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println("Final score: " + part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
