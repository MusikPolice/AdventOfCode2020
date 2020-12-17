package ca.jonathanfritz.aoc2020.day17;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part2 {

    private long solve(List<String> input) {
        // there's an infinite 3-dimensional cube
        final Map<Point, Boolean> activePoints = new HashMap<>();

        // it has some initial state
        for (int y = 0; y < input.size(); y++) {
            final String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                activePoints.put(new Point(x, y, 0, 0), line.charAt(x) == '#');
            }
        }

        // it executes six boot cycles
        for (int i = 0; i < 6; i++) {

            // make a copy of the cube so that we can modify it while still referencing the original
            final Map<Point, Boolean> copyOfPoints = new HashMap<>(activePoints);

            // iterate over all points in the existing cube as well as their neighbouring points
            final List<Point> allPointsInCube = getAllPointsInCube(activePoints).stream()
                    .map(point -> Stream.concat(Stream.of(point), point.getNeighbours().stream()))
                    .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream)
                    .distinct()
                    .collect(Collectors.toList());
            for (final Point p : allPointsInCube) {

                // determine if each the point remains active by inspecting its neighbours
                final List<Point> neighbours = p.getNeighbours();
                final boolean isCurrentlyActive = activePoints.getOrDefault(p, false);
                final long numActiveNeighbours = neighbours.stream()
                        .filter(n -> activePoints.getOrDefault(n, false))
                        .count();
                final boolean remainsActive = (isCurrentlyActive && (numActiveNeighbours == 2 || numActiveNeighbours == 3))
                        || (!isCurrentlyActive && numActiveNeighbours == 3);
                copyOfPoints.put(p, remainsActive);
            }

            // put the copy back into the original
            activePoints.clear();
            activePoints.putAll(copyOfPoints);
        }

        return activePoints.entrySet().stream()
                .filter(Map.Entry::getValue)
                .count();
    }

    // finds the max/min x,y,z,w values in the current space and generates a list of all points within those extents
    private List<Point> getAllPointsInCube(Map<Point, Boolean> activePoints) {
        final Extent xExtent = new Extent();
        final Extent yExtent = new Extent();
        final Extent zExtent = new Extent();
        final Extent wExtent = new Extent();

        for (Point p : activePoints.keySet()) {
            xExtent.update(p.x);
            yExtent.update(p.y);
            zExtent.update(p.z);
            wExtent.update(p.w);
        }

        return IntStream.rangeClosed(xExtent.min, xExtent.max)
                .mapToObj(x -> IntStream.rangeClosed(yExtent.min, yExtent.max)
                        .mapToObj(y -> IntStream.rangeClosed(zExtent.min, zExtent.max)
                                .mapToObj(z -> IntStream.rangeClosed(wExtent.min, wExtent.max)
                                    .mapToObj(w -> new Point(x, y, z, w)))
                                .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream))
                        .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream))
                .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static class Extent {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        public void update(int value) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }
    }

    private static class Point {
        final int x;
        final int y;
        final int z;
        final int w;

        private Point(int x, int y, int z, int w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        // neighbours are any other cubes where any of their coordinates differ by at most 1
        public List<Point> getNeighbours() {
            return IntStream.rangeClosed(x - 1, x + 1)
                    .mapToObj(px -> IntStream.rangeClosed(y - 1, y + 1)
                            .mapToObj(py -> IntStream.rangeClosed(z - 1, z + 1)
                                    .mapToObj(pz -> IntStream.rangeClosed(w - 1, w +1)
                                            .mapToObj(pw -> new Point(px, py, pz, pw)))
                                    .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream))
                            .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream))
                    .flatMap((Function<Stream<Point>, Stream<Point>>) pointStream -> pointStream)
                    .filter(p -> !(p.x == x && p.y == y && p.z == z && p.w == w))
                    .distinct()
                    .collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y &&
                    z == point.z &&
                    w == point.w;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, w);
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day17.txt")
                .collect(Collectors.toList());

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
