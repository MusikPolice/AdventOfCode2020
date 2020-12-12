package ca.jonathanfritz.aoc2020.day10;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<Long> input) {
        final Tree tree = new Tree(0, input);
        return tree.countPaths();
    }

    private static class Tree {
        final Map<Long, Set<Long>> tree = new HashMap<>();

        private Tree(long rootValue, List<Long> adaptors) {
            final Stack<Long> stack = new Stack<>();
            stack.push(rootValue);

            while(!stack.isEmpty()) {
                final long cur = stack.pop();
                if (tree.containsKey(cur)) {
                    continue;
                }
                final Set<Long> leaves = adaptors.stream()
                        .filter(adaptor -> adaptor - cur >= 1)
                        .filter(adaptor -> adaptor - cur <= 3)
                        .peek(stack::push)
                        .collect(Collectors.toSet());
                if (!leaves.isEmpty()) {
                    tree.put(cur, leaves);
                }
            }
        }

        private long countPaths() {
            final Map<Long, Integer> numPathsToNode = new HashMap<>();
            numPathsToNode.put(0L, 1);

            final List<Long> nodes = tree.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.toList());
            nodes.add(0, 0L);

            for (int i = 1; i < nodes.size(); i++) {
                final long previous = nodes.get(i - 1);
                final long current = nodes.get(i);

                final int numPathsBetweenNodes = findNumPathsBetween(previous, current);
                numPathsToNode.put(current, numPathsToNode.get(previous) + numPathsBetweenNodes);
            }

            return numPathsToNode.get(nodes.stream().max(Long::compareTo).get());
        }

        private int findNumPathsBetween(final long previous, final long current) {
            // use a stack to traverse the tree depth-first
            final Stack<Long> stack = new Stack<>();
            stack.push(previous);

            int count = 0;
            while (!stack.isEmpty()) {
                final long cur = stack.pop();

                final Set<Long> leaves = tree.get(cur);
                if (leaves != null) {
                    if (leaves.contains(current)) {
                        break;
                    }
                    if (leaves.size() > 1) {
                        count += leaves.size();
                    }

                    // add leaves that we haven't visited to the stack
                    leaves.forEach(stack::push);
                }
            }
            return count;
        }
    }

    public static void main (String[] args) {
        final List<Long> input = Utils.loadFromFile("day10.txt")
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));
    }
}
