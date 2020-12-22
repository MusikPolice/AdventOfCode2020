package ca.jonathanfritz.aoc2020.day22;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<String> input) {
        // player decks are modelled as FIFO queues
        final Queue<Integer> player1 = new LinkedList<>();
        final Queue<Integer> player2 = new LinkedList<>();

        // initialize the decks
        parseInput(input, player1, player2);

        int winner = combat(player1, player2, 1);
        System.out.printf("The winner of game 1 is player %d!%n", winner);
        System.out.printf("%n== Post-game results ==%n");
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

    private int combat(Queue<Integer> player1, Queue<Integer> player2, int game) {
        final Set<String> previousRoundStates = new HashSet<>();
        int nextGame = game;

        System.out.printf("%n=== Game %d ===%n%n", game);

        // play until one deck holds all the cards
        int round = 1;
        while (!(player1.isEmpty() || player2.isEmpty())) {
            // Before either player deals a card, if there was a previous round in this game that had exactly the same
            // cards in the same order in the same players' decks, the game instantly ends in a win for player 1
            final String deck1State = player1.stream().map(String::valueOf).collect(Collectors.joining(", "));
            final String deck2State = player2.stream().map(String::valueOf).collect(Collectors.joining(", "));
            System.out.printf("-- Round %d (Game %d) --%n", round, game);
            System.out.printf("Player 1's deck: %s%n", deck1State);
            System.out.printf("Player 2's deck: %s%n", deck2State);

            final String gameState = String.format("%s%s", deck1State, deck2State);
            if (previousRoundStates.contains(gameState)) {
                // player 1 wins
                System.out.println("Game state repeated - player 1 wins");
                return 1;
            } else {
                previousRoundStates.add(gameState);
            }

            // otherwise, each player draws a card
            int player1Card = player1.poll();
            int player2Card = player2.poll();
            System.out.printf("Player 1 plays: %d%n", player1Card);
            System.out.printf("Player 2 plays: %d%n", player2Card);

            final int roundWinner;
            if (player1.size() >= player1Card && player2.size() >= player2Card) {
                // If both players have at least as many cards remaining in their deck as the value of the card they
                // just drew, the winner of the round is determined by playing a new game of Recursive Combat
                // Each player creates a new deck by making a copy of the next cards in their deck (the quantity of
                // cards copied is equal to the number on the card they drew to trigger the sub-game)
                System.out.println("Playing a sub-game to determine the winner...");
                final Queue<Integer> deck1 = player1.stream().limit(player1Card).collect(Collectors.toCollection(LinkedList::new));
                final Queue<Integer> deck2 = player2.stream().limit(player2Card).collect(Collectors.toCollection(LinkedList::new));
                roundWinner = combat(deck1, deck2, ++nextGame);
                System.out.printf("%n...anyway, back to game %d.%n", game);

            } else {
                // the round is played as normal
                if (player1Card > player2Card) {
                    roundWinner = 1;
                } else {
                    roundWinner = 2;
                }
            }

            if (roundWinner == 1) {
                System.out.printf("Player 1 wins round %d of game %d!%n", round, game);
                player1.add(player1Card);
                player1.add(player2Card);
            } else {
                System.out.printf("Player 2 wins round %d of game %d!%n", round, game);
                player2.add(player2Card);
                player2.add(player1Card);
            }

            System.out.println("");
            round++;
        }

        // if deck1 is empty then player 2 won, and vice versa
        return player1.isEmpty() ? 2 : 1;
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

        final Part2 part2 = new Part2();
        System.out.println("Final score: " + part2.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
