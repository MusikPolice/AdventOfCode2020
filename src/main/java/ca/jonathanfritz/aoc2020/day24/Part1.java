package ca.jonathanfritz.aoc2020.day24;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        final Floor floor = new Floor();

        for (String line : input) {
            final List<Direction> directions = parseLine(line);
            floor.flip(directions);
        }

        return floor.getFlippedTiles();
    }

    private List<Direction> parseLine(String line) {
        final List<Direction> directions = new ArrayList<>();

        final Queue<String> instructions = new LinkedList<>(Utils.splitIntoChars(line));
        while (!instructions.isEmpty()) {
            final String next;
            switch (instructions.poll()) {
                case "n":
                    next = instructions.poll();
                    if (next.equals("e")) {
                        directions.add(Direction.NORTHEAST);
                    } else if (next.equals("w")) {
                        directions.add(Direction.NORTHWEST);
                    }
                    break;
                case "e":
                    directions.add(Direction.EAST);
                    break;
                case "s":
                    next = instructions.poll();
                    if (next.equals("e")) {
                        directions.add(Direction.SOUTHEAST);
                    } else if (next.equals("w")) {
                        directions.add(Direction.SOUTHWEST);
                    }
                    break;
                case "w":
                    directions.add(Direction.WEST);
                    break;
            }
        }

        return directions;
    }

    private enum Direction {
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTHWEST,
        WEST,
        NORTHWEST;
    }

    private static class Floor {
        private int flippedTiles = 0;
        private final Map<HexPoint, Tile> tiles = new HashMap<>();
        private final Tile reference = new Tile(tiles);

        public void flip(List<Direction> directions) {
            // starting at the reference tile, follow the directions to find the destination tile
            Tile currentTile = reference;
            for (Direction d : directions) {
                currentTile = currentTile.go(d, tiles);
            }

            // once there, flip the destination tile
            if (currentTile.flip()) {
                flippedTiles++;
            } else {
                flippedTiles--;
            }
        }

        public int getFlippedTiles() {
            return flippedTiles;
        }
    }

    private static class Tile {
        private boolean isFlipped = false;
        private final Map<Direction, Tile> neighbours = new HashMap<>();
        private final HexPoint address;

        // only used to initialize the reference tile
        public Tile(Map<HexPoint, Tile> tiles) {
            this.address = new HexPoint(0, 0);
            tiles.put(address, this);

            // create all of our neighbours so that we have somewhere to go
            for (Direction d : Direction.values()) {
                HexPoint neighbourAddress = address.move(d);
                final Tile neighbour = new Tile(neighbourAddress);
                tiles.put(neighbourAddress, neighbour);
                neighbours.put(d, neighbour);
            }
        }

        // tiles other than the reference tile just get an address
        public Tile(HexPoint address) {
            this.address = address;
        }

        // moves from the current tile in the specified direction
        public Tile go(Direction d, Map<HexPoint, Tile> tiles) {
            final Tile neighbour = neighbours.get(d);

            // only initialize neighbours the first time we visit them
            // this prevents the entire grid from being initialized all at once
            neighbour.initializeNeighbours(tiles);

            return neighbour;
        }

        public void initializeNeighbours(Map<HexPoint, Tile> tiles) {
            for (Direction d : Direction.values()) {
                final HexPoint neighbourAddress = address.move(d);
                if (tiles.containsKey(neighbourAddress)) {
                    // we already know about this tile
                    neighbours.put(d, tiles.get(neighbourAddress));
                } else {
                    // this is a brand new tile
                    final Tile newNeighbour = new Tile(neighbourAddress);
                    tiles.put(neighbourAddress, newNeighbour);
                    neighbours.put(d, newNeighbour);
                }
            }
        }

        private boolean flip() {
            isFlipped = !isFlipped;
            return isFlipped;
        }

        @Override
        public String toString() {
            return "[" + address.col + "," + address.row + "]:" + (isFlipped ? "black" : "white");
        }
    }

    private static class HexPoint {
        private final int col;
        private final int row;

        private HexPoint(int col, int row) {
            this.col = col;
            this.row = row;
        }

        // for an explanation of this hexagon coordinate system, see:
        // https://www.gamedev.net/articles/programming/general-and-gameplay-programming/coordinates-in-hexagon-based-tile-maps-r1800/
        public HexPoint move(Direction d) {
            switch (d) {
                case NORTHEAST:
                    if (row % 2 == 0) {
                        return new HexPoint(col, row - 1);
                    }
                    return new HexPoint(col + 1, row - 1);
                case EAST:
                    return new HexPoint(col + 1, row);
                case SOUTHEAST:
                    if (row % 2 == 0) {
                        return new HexPoint(col, row + 1);
                    }
                    return new HexPoint(col + 1, row + 1);
                case SOUTHWEST:
                    if (row % 2 == 0) {
                        return new HexPoint(col - 1, row + 1);
                    }
                    return new HexPoint(col, row + 1);
                case WEST:
                    return new HexPoint(col - 1, row);
                case NORTHWEST:
                    if (row % 2 == 0) {
                        return new HexPoint(col - 1, row - 1);
                    }
                    return new HexPoint(col, row - 1);
            }
            throw new IllegalArgumentException("Unrecognized Direction " + d.name());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HexPoint hexPoint = (HexPoint) o;
            return col == hexPoint.col &&
                    row == hexPoint.row;
        }

        @Override
        public int hashCode() {
            return Objects.hash(col, row);
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day24.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
