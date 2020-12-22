package ca.jonathanfritz.aoc2020.day20;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part1 {

    private long solve(List<String> input) {
        final List<Tile> tiles = parse(input);

        // sort tiles into a map of edge to the list of tiles that contain that edge
        final Map<String, List<Tile>> edgeMap = new HashMap<>();
        for (Tile tile : tiles) {
            for (String edge : tile.getEdges().asList()) {
                if (!edgeMap.containsKey(edge)) {
                    edgeMap.put(edge, Collections.singletonList(tile));
                } else {
                    final List<Tile> existingTiles = new ArrayList<>(edgeMap.get(edge));
                    existingTiles.add(tile);
                    edgeMap.put(edge, existingTiles);
                }
            }
        }

        // for each tile, we can find the set of other tiles that could be placed adjacent to it
        // problem is, this doesn't take rotations or flips into account - there are four rotations and 3 flips, so a
        // tile can be in at most 81 different orientations. Brute force is not your friend.
        final Map<Tile, List<Tile>> neighbours = new HashMap<>();
        for (Tile tile : tiles) {
            neighbours.put(tile, tile.getEdges()
                    .asList().stream()
                    .map(edgeMap::get)
                    .flatMap((Function<List<Tile>, Stream<Tile>>) Collection::stream)
                    .filter(t -> t != tile)
                    .distinct()
                    .collect(Collectors.toList()));
        }

        // hypothetically:
        // List<Tile> allPossibleTileOrientations = tiles.forEach(generateAllOrientations(tile)); // max size is 81 * num tiles
        // Map<Tile, List<Tile>> neighbours = ... // use above algorithm to find all possible neighbours for each tile orientation
        // maybe filter neighbours by only allowing map values where list contains four tiles, each derived from a different base tile
        // Map<Tile, List<Tile>> tileDerivations = ... // value contains all possible orientations of the key
        // for each tileDerivation
        //      find all possible north neighbours from derivations of other base tiles
        //      for each north neighbour
        //          find all possible east neighbours from derivations of other base tiles (not including middle or north derivations)
        //              find all possible south neighbours (as above)
        //                  find all possible west neighbours (as above)
        // repeat the neighbour search for each of the potential neighbours that we identified, again keeping track of
        // base tiles whose derivations can't be used
        // searches will start to fail as you move out and no possible neighbours can be found in the set of allowed
        // derivations. Problem is keeping track of all this state.

        // find border tiles - this may not work at scale. Turns out that border edges are not unique and could match
        // some other non-border edge. Fuck.
        final List<Tile> borderTiles = new ArrayList<>();
        for (Map.Entry<String, List<Tile>> entry : edgeMap.entrySet()) {
            if (entry.getValue().size() == 1) {
                final Tile tile = entry.getValue().get(0);
                if (tile.getEdges().asList().contains(entry.getKey())) {
                    if (!borderTiles.contains(tile)) {
                        borderTiles.add(tile);
                    }
                }
            }
        }

        // four of those are corner tiles
        final List<Tile> cornerTiles = new ArrayList<>();
        for (Tile tile : borderTiles) {
            final List<String> edges = tile.getEdges().asList();
            final boolean isCorner = edges.stream()
                    .filter(edge -> edgeMap.get(edge).size() == 1 && edgeMap.get(edge).get(0) == tile)
                    .count() == 2;
            if (isCorner && !cornerTiles.contains(tile)) {
                cornerTiles.add(tile);
            }
        }

        // multiply together the ids of the four corner tiles
        return cornerTiles.stream()
                .map(t -> t.id)
                .reduce(1L, (a, b) -> a * b);
    }

    private List<Tile> parse(List<String> input) {
        final List<Tile> tiles = new ArrayList<>();

        long id = 0;
        List<String> tileLines = new ArrayList<>();
        for (String line : input) {
            if (line.trim().length() == 0) {
                tiles.add(finishOpenTile(id, tileLines));
            } else if (line.startsWith("Tile")) {
                // open a new tile
                id = Long.parseLong(line.substring(line.lastIndexOf(" ") + 1, line.length() - 1));
                tileLines = new ArrayList<>();
            } else {
                // add to existing tile
                tileLines.add(line);
            }
        }
        if (tileLines.size() > 0) {
            tiles.add(finishOpenTile(id, tileLines));
        }

        return tiles;
    }

    private Tile finishOpenTile(long id, List<String> tileLines) {
        final String[][] pixels = new String[tileLines.size()][tileLines.get(0).length()];
        for (int i = 0; i < tileLines.size(); i++) {
            pixels[i] = Utils.splitIntoChars(tileLines.get(i)).toArray(new String[0]);
        }
        return new Tile(id, pixels);
    }

    private static class Tile {
        final long id;

        // a row,col array of pixels that make up this tile
        final String[][] pixels;

        private Tile(long id, String[][] pixels) {
            this.id = id;
            this.pixels = pixels;
        }

        // returns the four strings that represent the top, right, bottom, and left edges of this tile
        private Edges getEdges() {
            final String top = String.join("", pixels[0]);
            final String right = Arrays.stream(pixels)
                    .map(row -> row[row.length - 1])
                    .collect(Collectors.joining());
            final String bottom = String.join("", pixels[pixels.length - 1]);
            final String left = Arrays.stream(pixels)
                    .map(row -> row[0])
                    .collect(Collectors.joining());
            return new Edges(top, right, bottom, left);
        }

        // rotates the pixels of the tile clockwise
        private void rotateClockwise() {
            final int size = pixels.length;
            final String[][] rotated = new String[size][size];

            for (int row = 0; row < size; ++row) {
                for (int col = 0; col < size; ++col) {
                    rotated[row][col] = pixels[size - col - 1][row];
                }
            }

            for (int row = 0; row < size; ++row) {
                for (int col = 0; col < size; ++col) {
                    pixels[row][col] = rotated[row][col];
                }
            }
        }

        private void flipHorizontal() {
            final int size = pixels.length;
            for (int row = 0; row < size / 2; row++) {
                final String[] temp = pixels[size - row];
                pixels[size - row] = pixels[row];
                pixels[row] = temp;
            }
        }

        private void flipVertical() {
            final int size = pixels.length;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size / 2; col++) {
                    final String temp = pixels[row][size - col];
                    pixels[row][size - col] = pixels[row][col];
                    pixels[row][col] = temp;
                }
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(String.format("Tile %d:%n", id));
            for (int row = 0; row < pixels.length; row++) {
                sb.append(String.format("%s%n",String.join("", pixels[row])));
            }
            sb.append(String.format("%n"));
            return sb.toString();
        }

        private static class Edges {
            private final String top;
            private final String right;
            private final String bottom;
            private final String left;

            private Edges(String top, String right, String bottom, String left) {
                this.top = top;
                this.right = right;
                this.bottom = bottom;
                this.left = left;
            }

            private List<String> asList() {
                return Arrays.asList(top, right, bottom, left);
            }
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day20.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
