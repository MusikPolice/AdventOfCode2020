package ca.jonathanfritz.aoc2020.day12;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Part2 {

    private int solve(List<String> input) {
        Point ship = new Point(0, 0);
        Point waypoint = new Point(10, 1);

        for (String instruction : input) {
            final int units  = Integer.parseInt(instruction.substring(1));
            switch (instruction.substring(0, 1)) {
                case "N":
                    waypoint = waypoint.move(Direction.North, units);
                    break;
                case "S":
                    waypoint = waypoint.move(Direction.South, units);
                    break;
                case "E":
                    waypoint = waypoint.move(Direction.East, units);
                    break;
                case "W":
                    waypoint = waypoint.move(Direction.West, units);
                    break;
                case "L":
                    // rotate waypoint about the ship counter-clockwise
                    for (int i = 0; i < (units / 90); i++) {
                        // east (+) becomes north (+), west (-) becomes south (-)
                        final int north = waypoint.east;

                        // north (+) becomes west (-), south (-) becomes east (+)
                        final int east = waypoint.north * -1;

                        waypoint = new Point(east, north);
                    }
                    break;
                case "R":
                    // rotate waypoint about the ship clockwise
                    for (int i = 0; i < (units / 90); i++) {
                        // east (+) becomes south (-), west (-) becomes north (+)
                        final int north = waypoint.east * -1;

                        // north (+) becomes east (+), south (-) becomes west (-)
                        final int east = waypoint.north;

                        waypoint = new Point(east, north);
                    }
                    break;
                case "F":
                    // move toward the waypoint in each direction
                    ship = ship.move(Direction.North, waypoint.north * units)
                            .move(Direction.East, waypoint.east * units);
                    break;
            }
            System.out.println(instruction + " - Ship: " + ship.toString() + ", Waypoint: " + waypoint.toString());
        }

        return Math.abs(ship.east) + Math.abs(ship.north);
    }

    private enum Direction {
        North,
        East,
        South,
        West;
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

        final Part2 part2 = new Part2();
        System.out.println("Manhattan distance moved: " + part2.solve(input));
    }
}
