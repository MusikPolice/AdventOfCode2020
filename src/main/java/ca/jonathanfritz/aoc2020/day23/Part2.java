package ca.jonathanfritz.aoc2020.day23;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Part2 {

    private static final long MIN = 1;
    private static final long MAX = 1000000;

    private long solve(List<String> input) {
        // input is a single string of integers, each character representing a cup with a label between 1 and 9 inclusive
        final LinkedValueList cups = new LinkedValueList(10);
        Utils.splitIntoChars(input.get(0)).stream()
                .map(Long::parseLong)
                .forEach(cups::add);

        // pad the list out to 1 million cups
        LongStream.rangeClosed(10, MAX)
                .forEach(cups::add);

        // the crab does ten million moves, starting with the leftmost cup in the line
        long currentCupLabel = cups.head.value;
        for (int move = 0; move < 10000000; move++) {

            // The crab picks up the three cups that are immediately clockwise of the current cup. They are removed from
            // the circle; cup spacing is adjusted as necessary to maintain the circle.
            final List<Long> removed = cups.removeAfter(currentCupLabel, 3).stream()
                    .map(node -> node.value)
                    .collect(Collectors.toList());

            // The crab selects a destination cup: the cup with a label equal to the current cup's label minus one.
            long destinationCupLabel = cups.get(currentCupLabel).value - 1;
            do {
                // If at any point in this process the value goes below the lowest value on any cup's label, it wraps
                // around to the highest value on any cup's label instead.
                if (destinationCupLabel < MIN) {
                    destinationCupLabel = MAX;
                }

                // If this would select one of the cups that was just picked up, the crab will keep subtracting one
                // until it finds a cup that wasn't just picked up.
                if (removed.contains(destinationCupLabel)) {
                    destinationCupLabel--;
                }
            } while (!cups.contains(destinationCupLabel));

            // The crab places the cups it just picked up so that they are immediately clockwise of the destination cup.
            // They keep the same order as when they were picked up.
            cups.addAfter(destinationCupLabel, removed.stream()
                    .map(LinkedValueList.Node::new)
                    .collect(Collectors.toList())
            );

            // The crab selects a new current cup: the cup which is immediately clockwise of the current cup.
            final LinkedValueList.Node currentNode = cups.get(currentCupLabel);
            if (currentNode == cups.tail) {
                // we are at the end of the list, loop around to the start
                currentCupLabel = cups.head.value;
            } else {
                currentCupLabel = currentNode.next.value;
            }
        }

        // The crab is going to hide your stars under the two cups that will end up immediately clockwise of cup 1
        final LinkedValueList.Node one = cups.get(1);
        final long a = one.next.value;
        final long b = one.next.next.value;
        return a * b;
    }

    private static class LinkedValueList {
        private Node head;
        private Node tail;
        private final Map<Long, Node> valueMap;

        public LinkedValueList(int size) {
            valueMap = new HashMap<>(size);
        }

        Node get(long value) {
            return valueMap.get(value);
        }

        Node remove(long value) {
            final Node node = valueMap.remove(value);
            if (node == null) {
                // value isn't in the list
                return null;
            } else if (node == head) {
                // found the start of the list
                head = node.next;
                head.previous = null;
            } else if (node == tail) {
                // found the end of the list
                tail = node.previous;
                tail.next = null;
            } else {
                final Node previous = node.previous;
                final Node next = node.next;

                previous.next = next;
                next.previous = previous;
            }
            valueMap.remove(node.value);
            return node;
        }

        List<Node> removeAfter(long value, int numNodesToRemove) {
            final List<Node> removed = new ArrayList<>();

            final Node node = valueMap.get(value);
            Node next = node == tail ? head : node.next;
            for (int i = 0; i < numNodesToRemove; i++) {
                Node n = remove(next.value);
                removed.add(n);

                if (n.next == null) {
                    // we just removed the tail - wrap around to head
                    next = head;
                } else {
                    next = n.next;
                }
            }
            return removed;
        }

        void add(long value) {
            final Node newNode = new Node(value);
            if (head == null) {
                // list is empty
                head = newNode;
            } else {
                // add to end of list
                tail.next = newNode;
                newNode.previous = tail;
            }
            tail = newNode;
            valueMap.put(value, newNode);
        }

        void addAfter(long value, List<Node> nodesToAdd) {
            Node node = valueMap.get(value);
            final Node next = node.next;

            for (Node n : nodesToAdd) {
                valueMap.put(n.value, n);
                node.next = n;
                n.previous = node;
                node = n;
            }

            if (next == null) {
                // we were adding after tail, so node becomes new tail
                tail = node;
            } else {
                node.next = next;
                next.previous = node;
            }
        }

        final boolean contains(long value) {
            return valueMap.containsKey(value);
        }

        public List<Long> asList() {
            if (head == null) {
                return new ArrayList<>();
            }
            final List<Long> list = new ArrayList<>();
            Node node = head;
            while (node != null) {
                list.add(node.value);
                node = node.next;
            }
            return list;
        }

        private static class Node {
            private final Long value;
            private Node previous;
            private Node next;

            private Node(Long value) {
                this.value = value;
            }
        }
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day23.txt")
                .collect(Collectors.toList());

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
