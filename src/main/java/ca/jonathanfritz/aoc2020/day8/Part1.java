package ca.jonathanfritz.aoc2020.day8;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        // program counter - current position in instruction list
        int pc = 0;

        // accumulator - the only register in our tiny CPU :)
        int acc = 0;

        // set of previously executed pc positions
        // if we hit one of these for a second time, we're in an infinite loop
        final Set<Integer> visited = new HashSet<>();

        // execute until we hit an instruction that we've already seen
        while (!visited.contains(pc)) {
            visited.add(pc);

            final String inst = input.get(pc);
            final String op = inst.substring(0, 3);
            final int arg = Integer.parseInt(inst.substring(4));
            switch (op) {
                case "nop":
                    pc++;
                    break;
                case "acc":
                    acc += arg;
                    pc++;
                    break;
                case "jmp":
                    pc += arg;
                    break;
            }
        }
        return acc;
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day8.txt").collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println("Value of acc immediately before infinite loop: " + part1.solve(input));
    }
}
