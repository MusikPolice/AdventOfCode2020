package ca.jonathanfritz.aoc2020.day13;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Part2 {

    private long solve(List<String> input) {
        // map key is minute offset, map value is bus id that should depart at that offset
        final String[] schedule = input.get(1).split(",");
        final Map<Integer, Integer> map = new HashMap<>();
        for (int minute = 0; minute < schedule.length; minute++) {
            final String busId = schedule[minute];
            if ("x".equals(busId)) {
                continue;
            }
            map.put(minute, Integer.parseInt(busId));
        }

        // we know the target time has to be near a multiple of every bus id, so if we iterate by the largest
        // bus id, we cover more ground much faster
        final Map.Entry<Integer, Integer> largestEntry = map.entrySet().stream().max(Map.Entry.comparingByValue()).get();
        final int posOffset = largestEntry.getKey();
        final int iterator = largestEntry.getValue();

        // check to see if the rest of the busses depart at the correct offsets after each departure of the first bus
        // this is a terrible solution - given the size of the input, this iterates some 1.4 trillion times before landing on the answer
        for (long minute = iterator - posOffset ;; minute += iterator) {
            boolean found = true;
            for (int offset : map.keySet()) {
                final int busId = map.get(offset);
                if ((minute + offset) % busId != 0) {
                    // current time is not a multiple of busId
                    found = false;
                    break;
                }
            }
            if (found) {
                // this is the time at which the busses all depart one minute after another!
                return minute;
            }
        }
    }

    public static void main(String[] args) {
        final List<String> input = Utils.loadFromFile("day13.txt")
                .collect(Collectors.toList());

        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));
    }
}
