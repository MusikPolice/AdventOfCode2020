package ca.jonathanfritz.aoc2020.day3;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part2 {

    public long solve(List<String> input) {
        // sort the input into a double array
        final int lineLength = input.get(0).length();
        final char[][] map = new char[input.size()][lineLength];
        for (int i = 0; i < input.size(); i++) {
            final String line = input.get(i);
            map[i] = line.toCharArray();
        }

        // tracks current position, handles modding position when it exceeds map boundaries
        final Position currentPosition = new Position(lineLength, input.size());

        return Stream.of(
                countNumTrees(map, currentPosition, 1, 1),
                countNumTrees(map, currentPosition, 3, 1),
                countNumTrees(map, currentPosition, 5, 1),
                countNumTrees(map, currentPosition, 7, 1),
                countNumTrees(map, currentPosition, 1, 2)
        )
        .peek(System.out::println)
        .reduce(1L, (x, y) -> x * y);
    }

    private long countNumTrees(char[][] map, Position currentPosition, int rightSlope, int downSlope) {
        // iterate over rows, counting the trees that we run into
        currentPosition.reset();
        int treeCount = 0;
        for (int y = 0; y < map.length; y+= downSlope) {
            currentPosition.move(rightSlope, downSlope);
            if ('#' == map[currentPosition.row][currentPosition.col]) {
                treeCount++;
            }
        }
        return treeCount;
    }

    private static class Position {

        private int col = 0;
        private int row = 0;

        private final int maxCol;
        private final int maxRow;

        public Position(int maxCol, int maxRow) {
            this.maxCol = maxCol;
            this.maxRow = maxRow;
        }

        public void move(int right, int down) {
            col = (col + right) % maxCol;
            row = Math.min(row + down, maxRow - 1);
        }

        public void reset() {
            col = 0;
            row = 0;
        }
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day3.txt")
                .collect(Collectors.toList());

        /*List<String> input = Arrays.asList(
                "..##.......",
                "#...#...#..",
                ".#....#..#.",
                "..#.#...#.#",
                ".#...##..#.",
                "..#.##.....",
                ".#.#.#....#",
                ".#........#",
                "#.##...#...",
                "#...##....#",
                ".#..#...#.#"
        );*/

        final Part2 part1 = new Part2();
        System.out.println(part1.solve(input));
    }
}
