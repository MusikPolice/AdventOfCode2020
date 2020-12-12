package ca.jonathanfritz.aoc2020.day12;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        Point pos = new Point(0, 0);
        Direction dir = Direction.East;

        for (String instruction : input) {
            final int units  = Integer.parseInt(instruction.substring(1));
            switch (instruction.substring(0, 1)) {
                case "N":
                    pos = pos.move(Direction.North, units);
                    break;
                case "S":
                    pos = pos.move(Direction.South, units);
                    break;
                case "E":
                    pos = pos.move(Direction.East, units);
                    break;
                case "W":
                    pos = pos.move(Direction.West, units);
                    break;
                case "L":
                    for (int i = 0; i < (units / 90); i++) {
                        dir = Direction.rotateLeft(dir);
                    }
                    break;
                case "R":
                    for (int i = 0; i < (units / 90); i++) {
                        dir = Direction.rotateRight(dir);
                    }
                    break;
                case "F":
                    pos = pos.move(dir, units);
                    break;
            }
            System.out.println(instruction + " " + pos.toString() + " facing " + dir);
        }

        return Math.abs(pos.east) + Math.abs(pos.north);
    }

    private enum Direction {
        North,
        East,
        South,
        West;

        static Direction rotateLeft(Direction currentDirection) {
            int newOrdinal = currentDirection.ordinal() - 1;
            if (newOrdinal == -1) {
                newOrdinal = West.ordinal();
            }
            return Direction.values()[newOrdinal];
        }

        static Direction rotateRight(Direction currentDirection) {
            int newOrdinal = currentDirection.ordinal() + 1;
            if (newOrdinal == Direction.values().length) {
                newOrdinal = North.ordinal();
            }
            return Direction.values()[newOrdinal];
        }
    }

    private static class Point {
        final int east;
        final int north;

        private Point(int east, int north) {
            this.east = east;
            this.north = north;
        }

        Point move(Direction direction, int amount) {
            switch (direction) {
                case North:
                    return new Point(east, north + amount);
                case East:
                    return new Point(east + amount, north);
                case South:
                    return new Point(east, north - amount);
                case West:
                default:
                    return new Point(east - amount, north);
            }
        }

        @Override
        public String toString() {
            return "(" + east + "," + north + ")";
        }
    }

    public static void main(String args[]) {
        final List<String> input = Utils.loadFromFile("day12.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println("Manhattan distance moved: " + part1.solve(input));
    }
}
