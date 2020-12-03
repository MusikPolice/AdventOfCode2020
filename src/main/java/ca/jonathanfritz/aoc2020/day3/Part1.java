package ca.jonathanfritz.aoc2020.day3;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    public int solve(List<String> input) {
        // sort the input into a double array
        final int lineLength = input.get(0).length();
        final char[][] map = new char[input.size()][lineLength];
        for (int i = 0; i < input.size(); i++) {
            final String line = input.get(i);
            map[i] = line.toCharArray();
        }

        // tracks current position, handles modding position when it exceeds map boundaries
        final Position currentPosition = new Position(lineLength, input.size());

        // iterate over rows, counting the trees that we run into
        int treeCount = 0;
        for (int y = 0; y < input.size(); y++) {
            currentPosition.move(3, 1);
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
            row = (row + down) % maxRow;
        }
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day3.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
