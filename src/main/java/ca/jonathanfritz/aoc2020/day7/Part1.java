package ca.jonathanfritz.aoc2020.day7;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        final Graph graph = parseInput(input);
        final Node shinyGold = new Node("shiny gold");
        return graph.nodes.keySet().stream()
                .filter(n -> !n.equals(shinyGold))
                .mapToInt(outerBag -> {
                    if (graph.pathExists(outerBag, shinyGold)) {
                        System.out.printf("%s can contain %s%n", outerBag.colour, shinyGold.colour);
                        return 1;
                    }
                    System.out.printf("%s cannot contain %s%n", outerBag.colour, shinyGold.colour);
                    return 0;
                })
                .sum();
    }

    private Graph parseInput(List<String> input) {
        final Graph graph = new Graph();
        for (String line : input) {
            final List<String> tokens = Arrays.stream(
                    line.replace(",","")
                            .replace(".","")
                            .replace("bags contain", "")
                            .replace("no other bags", "")
                            .replace("bags", "")
                            .replace("bag", "")
                            .toLowerCase()
                            .split(" ")
            )
            .filter(s -> s.trim().length() > 0)
            .collect(Collectors.toList());

            final List<Node> nodes = new ArrayList<>();
            StringBuilder colour = new StringBuilder();
            for (String token : tokens) {
                if (isInteger(token.trim())) {
                    nodes.add(new Node(colour.toString()));
                    colour = new StringBuilder();
                    continue;
                }

                if (colour.length() != 0) {
                    colour.append(" ");
                }
                colour.append(token.trim());
            }
            if (colour.length() > 0) {
                nodes.add(new Node(colour.toString()));
            }

            final Node start = nodes.remove(0);
            graph.addRule(start, nodes);
        }
        return graph;
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    private static class Graph {
        final Map<Node, Set<Node>> nodes = new HashMap<>();

        public void addRule(Node start, List<Node> paths) {
            System.out.printf("%s -> [%s]%n", start.colour, paths.stream().map(n -> n.colour).collect(Collectors.joining(",")));
            if (!nodes.containsKey(start)) {
                nodes.put(start, new HashSet<>(paths));
            } else {
                final Set<Node> existingPaths = nodes.get(start);
                existingPaths.addAll(new HashSet<>(paths));
                nodes.put(start, existingPaths);
            }
        }

        public boolean pathExists(Node start, Node end) {
            // keep track of nodes we've visited to prevent loops
            final Set<Node> visited = new HashSet<>();

            // breadth-first search from start to end node
            final Stack<Node> stack = new Stack<>();
            stack.push(start);

            // search until stack is empty or end is found
            while (!stack.isEmpty()) {
                final Node cur = stack.pop();

                // prevent loops
                if (visited.contains(cur)) {
                    continue;
                }
                visited.add(cur);

                // did we find it?
                if (cur.equals(end)) {
                    return true;
                } else {
                    nodes.get(cur).forEach(stack::push);
                }
            }

            // no, no we didn't
            return false;
        }
    }

    private static class Node {
        private final String colour;

        private Node(String colour) {
            this.colour = colour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(colour, node.colour);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colour);
        }

        @Override
        public String toString() {
            return colour;
        }
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day7.txt").collect(Collectors.toList());
        final Part1 part2 = new Part1();
        System.out.println("Number of bags that can contain shiny gold: " + part2.solve(input));
    }
}
