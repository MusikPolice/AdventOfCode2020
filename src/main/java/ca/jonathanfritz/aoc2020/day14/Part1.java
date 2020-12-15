package ca.jonathanfritz.aoc2020.day14;

import ca.jonathanfritz.aoc2020.Utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Part1 {

    private long solve(List<String> input) {
        // key is memory address, value is the number stored at that address
        final Map<Long, Long> memory = new HashMap<>();

        String currentBitmask = "";
        for (String instruction : input) {
            final String valueString = instruction.substring(instruction.lastIndexOf(" ") + 1).trim();
            if (instruction.startsWith("mask")) {
                currentBitmask = leftPad(valueString, 64);

            } else if (instruction.startsWith("mem")) {
                // perform the bitmask with string ops b/c java byte is signed (among other problems)
                final long value = Long.parseLong(valueString);
                final String bits = leftPad(Long.toBinaryString(value), 64);
                final StringBuilder transformed = new StringBuilder();
                for (int i = 0; i < 64; i++) {
                    if ('1' == currentBitmask.charAt(i)) {
                        transformed.append("1");
                    } else if ('0' == currentBitmask.charAt(i)) {
                        transformed.append("0");
                    } else {
                        transformed.append(bits.charAt(i));
                    }
                }

                // store the transformed value in memory
                final int startOfAddress = instruction.indexOf("[") + 1;
                final int endOfAddress = instruction.indexOf("]");
                final long address = Long.parseLong(instruction.substring(startOfAddress, endOfAddress).trim());
                final long transformedValue = new BigInteger(transformed.toString(), 2).longValue();
                memory.put(address, transformedValue);
            }
        }

        // return sum of all values in memory
        return memory.values().stream()
                .reduce(Long::sum)
                .get();
    }

    public String leftPad(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        return sb.toString();
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day14.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}
