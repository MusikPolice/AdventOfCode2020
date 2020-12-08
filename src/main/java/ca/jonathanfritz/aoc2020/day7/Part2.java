package ca.jonathanfritz.aoc2020.day7;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<String> input) {
        final Graph graph = parseInput(input);
        final Node shinyGold = new Node("shiny gold");
        return graph.getNumBagsInside(shinyGold);
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
            final List<Integer> multipliers = new ArrayList<>();
            StringBuilder colour = new StringBuilder();
            for (String token : tokens) {
                if (isInteger(token.trim())) {
                    nodes.add(new Node(colour.toString()));
                    multipliers.add(Integer.parseInt(token));
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

            final Node parent = nodes.size() > 0 ? nodes.get(0) : null;
            final Integer leftWeight = multipliers.size() > 0 ? multipliers.get(0) : null;
            final Node left = nodes.size() > 1 ? nodes.get(1) : null;
            final Integer rightWeight = multipliers.size() > 1 ? multipliers.get(1) : null;
            final Node right = nodes.size() > 2 ? nodes.get(2) : null;
            graph.addRule(parent, new Rule(leftWeight, left, rightWeight, right));
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
        private final Map<Node, Rule> nodes = new HashMap<>();

        private void addRule(Node parent, Rule rule) {
            nodes.put(parent, rule);
        }

        private long getNumBagsInside(Node start) {
            long sum = 0;
            final Stack<Node> stack = new Stack<>();
            stack.push(start);

            while (!stack.isEmpty()) {
                final Node current = stack.pop();
                final Rule next = nodes.get(current);
                if (next == null) {
                    continue;
                }

                // look ahead down the left side
                final Rule leftRule = nodes.get(next.leftChild);
                if (leftRule != null && leftRule.hasChild()) {
                    sum += next.leftWeight;
                    sum += childSum(next.leftWeight, leftRule);
                    stack.push(next.leftChild);
                }

                // look ahead down the right side
                final Rule rightRule = nodes.get(next.rightChild);
                if (rightRule != null && rightRule.hasChild()) {
                    sum += next.rightWeight;
                    sum += childSum(next.rightWeight, rightRule);
                    stack.push(next.rightChild);
                }
            }

            return sum;
        }

        private long childSum(final int parentWeight, final Rule child) {
            if (child != null) {
                int childSum = (child.leftChild != null ? child.leftWeight : 0) + (child.rightChild != null ? child.rightWeight : 0);
                return parentWeight * childSum;
            }
            return 0;
        }
    }

    private static class Rule {
        private final int leftWeight;
        private final Node leftChild;

        private final int rightWeight;
        private final Node rightChild;

        private Rule(Integer leftWeight, Node leftChild, Integer rightWeight, Node rightChild) {
            this.leftWeight = leftWeight != null ? leftWeight : 1;
            this.leftChild = leftChild;
            this.rightWeight = rightWeight != null ? rightWeight : 1;
            this.rightChild = rightChild;
        }

        private boolean hasChild() {
            return leftChild != null || rightChild != null;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if (leftChild != null) {
                sb.append(String.format("%d %s bags", leftWeight, leftChild.toString()));
            }
            if (rightChild != null) {
                if (sb.length() > 0) {
                    sb.append(" and ");
                }
                sb.append(String.format("%d %s bags", rightWeight, rightChild.toString()));
            }
            return  sb.toString();
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

        /*final List<String> input = Arrays.asList(
                "light red bags contain 1 bright white bag, 2 muted yellow bags.",
                "dark orange bags contain 3 bright white bags, 4 muted yellow bags.",
                "bright white bags contain 1 shiny gold bag.",
                "muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.",
                "shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.",
                "dark olive bags contain 3 faded blue bags, 4 dotted black bags.",
                "vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.",
                "faded blue bags contain no other bags.",
                "dotted black bags contain no other bags."
        );*/

        final Part2 part2 = new Part2();
        System.out.println("A shiny gold bag must contain " + part2.solve(input) + " other bags!");
    }
}
