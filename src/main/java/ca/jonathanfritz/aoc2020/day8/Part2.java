package ca.jonathanfritz.aoc2020.day8;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Part2 {

    private int solve(List<String> input) {
        // find all instructions that could be flipped in an effort to find a normally terminating program
        final List<Integer> positionsToFlip = IntStream.range(0, input.size())
                .filter(pc -> input.get(pc).startsWith("nop") || input.get(pc).startsWith("jmp"))
                .boxed()
                .collect(Collectors.toList());

        for (int positionToFlip : positionsToFlip) {
            try {
                // take a copy of the original instruction set and flip the appropriate instruction
                final List<String> copyOfInput = new ArrayList<>(input);
                final String instruction = copyOfInput.remove(positionToFlip);
                final String newInstruction;
                if (instruction.startsWith("nop")) {
                    newInstruction = instruction.replace("nop", "jmp");
                } else if (instruction.startsWith("jmp")) {
                    newInstruction = instruction.replace("jmp", "nop");
                } else {
                    throw new IllegalStateException("You done fucked up");
                }
                System.out.printf("Changed instruction %d [%s] to [%s]%n", positionToFlip, instruction, newInstruction);
                copyOfInput.add(positionToFlip, newInstruction);

                // try it out - if we succeed, return the accumulator
                return run(copyOfInput);
            } catch (RuntimeException ex) {
                // expected
            }
        }

        throw new RuntimeException("Failed to find solution");
    }

    private int run(List<String> input) {
        // program counter - current position in instruction list
        int pc = 0;

        // accumulator - the only register in our tiny CPU :)
        int acc = 0;

        // set of previously executed pc positions
        // if we hit one of these for a second time, we're in an infinite loop
        final Set<Integer> visited = new HashSet<>();

        while (true) {
            if (visited.contains(pc)) {
                // we hit an infinite loop - program terminates exceptionally
                throw new RuntimeException("Infinite Loop!");
            }
            visited.add(pc);

            if (pc == input.size()) {
                // we've hit the instruction after the last instruction in the file!
                // program terminates normally
                return acc;
            }

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
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day8.txt").collect(Collectors.toList());
        final Part2 part1 = new Part2();
        System.out.println("Value of acc immediately before infinite loop: " + part1.solve(input));
    }
}
