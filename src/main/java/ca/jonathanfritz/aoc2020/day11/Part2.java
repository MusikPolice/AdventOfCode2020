package ca.jonathanfritz.aoc2020.day11;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part2 {

    private static final String AVAILABLE = "L";
    private static final String TAKEN = "#";
    private static final String FLOOR = ".";

    private int solve(List<String> input) {
        // assemble the map of seats
        String[][] seats = new String[input.size()][input.get(0).length()];
        for (int i = 0; i < input.size(); i++) {
            seats[i] = input.get(i).split("(?!^)");
        }

        while (true) {
            printSeatMap(seats);

            boolean changed = false;
            final String[][] temp = copy(seats);
            for (int row = 0; row < seats.length; row++) {
                for (int col = 0; col < seats[row].length; col++) {
                    switch (seats[row][col]) {
                        case AVAILABLE:
                            if (getNumVisibleOccupiedSeats(seats, row, col) == 0) {
                                temp[row][col] = TAKEN;
                                changed = true;
                            }
                            break;
                        case TAKEN:
                            if (getNumVisibleOccupiedSeats(seats, row, col) >= 5) {
                                temp[row][col] = AVAILABLE;
                                changed = true;
                            }
                            break;
                    }
                }
            }
            if (changed) {
                seats = copy(temp);
            } else {
                break;
            }
        }

        return getNumOccupiedSeats(seats);
    }

    private String[][] copy(String[][] seats) {
        return Arrays.stream(seats).map(String[]::clone).toArray(String[][]::new);
    }

    private int getNumVisibleOccupiedSeats(String[][] seats, int row, int col) {
        return isOccupied(seats, row, -1, col, 0)  // north
            + isOccupied(seats, row, -1, col, 1)   // north east
            + isOccupied(seats, row, 0,col, 1)     // east
            + isOccupied(seats, row, 1, col, 1)    // south east
            + isOccupied(seats, row, 1, col, 0)    // south
            + isOccupied(seats, row, 1, col, -1)   // south west
            + isOccupied(seats, row, 0, col, -1)   // west
            + isOccupied(seats, row, -1, col, -1); // north west
    }

    private int isOccupied(String[][] seats, int row, int rowOffset, int col, int colOffset) {
        try {
            int multiplier = 1;
            while (true) {
                final String seat = seats[row + (rowOffset * multiplier)][col + (colOffset * multiplier)];
                if (FLOOR.equals(seat)) {
                    multiplier++;
                } else {
                    return TAKEN.equals(seat) ? 1 : 0;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            // out of bounds is considered to be unoccupied
            return 0;
        }
    }

    private int getNumOccupiedSeats(String[][] seats) {
        int occupied = 0;
        for (String[] row : seats) {
            for (String seat : row) {
                if (TAKEN.equals(seat)) {
                    occupied++;
                }
            }
        }
        return occupied;
    }

    private void printSeatMap(String[][] seats) {
        for (String[] row : seats) {
            for (String seat : row) {
                System.out.print(seat);
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day11.txt")
                .collect(Collectors.toList());

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));
    }
}
