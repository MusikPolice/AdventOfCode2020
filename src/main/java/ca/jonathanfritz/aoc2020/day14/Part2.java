package ca.jonathanfritz.aoc2020.day14;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Part2 {

    private long solve(List<String> input) {
        // key is memory address, value is the number stored at that address
        final Map<Long, Long> memory = new HashMap<>();

        String bitmask = "";
        for (String instruction : input) {
            final String valueString = instruction.substring(instruction.lastIndexOf(" ") + 1).trim();
            if (instruction.startsWith("mask")) {
                // extract the new bitmask from the instruction
                bitmask = leftPad(valueString, 64);

            } else if (instruction.startsWith("mem")) {
                final long value = Long.parseLong(valueString);

                final int startOfAddress = instruction.indexOf("[") + 1;
                final int endOfAddress = instruction.indexOf("]");
                final String addressBits = leftPad(Long.toBinaryString(
                        Long.parseLong(instruction.substring(startOfAddress, endOfAddress).trim())), 64);

                // count the number of "floating" x characters in the current bitmask
                final int numFloating = (int) Arrays.stream(bitmask.split("(?!^)")).filter("x"::equalsIgnoreCase).count();

                // transform baseBitmask to decode all possible address masks that it represents
                final String[] currentBitmaskBits = bitmask.split("(?!^)");
                int numDecodedAddresses = (int) Math.pow(2, numFloating);
                IntStream.range(0, numDecodedAddresses)
                        .mapToObj(i -> {
                            // generates all address masks between 0 and 2^(numFloating - 1)
                            final String binary = leftPad(Long.toBinaryString(i), numFloating);
                            int offset = 0;
                            final StringBuilder newBitmask = new StringBuilder();
                            for (int j = 0; j < currentBitmaskBits.length; j++) {
                                final String maskBit = currentBitmaskBits[j];
                                if ("x".equalsIgnoreCase(maskBit)) {
                                    if ('1' == addressBits.charAt(j)) {
                                        newBitmask.append("1");
                                    } else {
                                        newBitmask.append(binary.charAt(offset));
                                    }
                                    offset++;
                                } else {
                                    newBitmask.append(maskBit);
                                }
                            }
                            return newBitmask.toString();
                        })
                        .map(addressMask -> {
                            // transforms baseBitmask into an address based on the incoming binary string
                            final StringBuilder address = new StringBuilder();
                            final String[] addressMaskBits = addressMask.split("(?!^)");
                            for (int i = 0; i < addressMaskBits.length; i++) {
                                if ("1".equalsIgnoreCase(addressMaskBits[i])) {
                                    // any 1s in the bitmask are logically ORd with the address bits
                                    address.append("1");
                                } else {
                                    // otherwise, we get this bit from the base address
                                    address.append(addressBits.charAt(i));
                                }
                            }
                            return address.toString();
                        })
                        .map(address -> new BigInteger(address, 2).longValue())
                        .forEach(address -> memory.put(address, value));
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
        /*final List<String> input = Utils.loadFromFile("day14.txt")
                .collect(Collectors.toList());*/

        final List<String> input = Arrays.asList(
                "mask = 000000000000000000000000000000X1001X",
                "mem[42] = 100"
        );

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));
    }
}
