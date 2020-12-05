package ca.jonathanfritz.aoc2020.day4;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part1 {

    private int solve(List<String> input) {
        // every group of lines is a passport
        final List<Passport> passports = new ArrayList<>();
        final List<String> lines = new ArrayList<>();
        for (String line : input) {
            lines.add(line);
            if (line.trim().length() == 0 && !lines.isEmpty()) {
                passports.add(new Passport(lines));
                lines.clear();
            }
        }
        if (!lines.isEmpty()) {
            passports.add(new Passport(lines));
        }

        // if valid, add one to the sum
        return passports.stream()
                .map(passport -> passport.isValid() ? 1 : 0)
                .mapToInt(value -> value)
                .sum();
    }

    private static class Passport {
        private final Map<Field, String> elements;

        public Passport(List<String> lines) {
            elements = getPassportElements(lines);
        }

        private Map<Field, String> getPassportElements(List<String> lines) {
            // start by concatenating all the strings that make up the passport into one master string,
            // and then splitting that master up by spaces between field:value pairs
            return Arrays.stream(String.join(" ", lines).split(" "))
                    .filter(s -> s != null && s.trim().length() != 0)
                    .map(s -> {
                        // then split each field:value pair and pack all into a map
                        final String[] keyvalue = s.split(":");
                        return Arrays.stream(Field.values())
                                .filter(f -> f.name().equalsIgnoreCase(keyvalue[0]))
                                .findFirst()
                                .map(field -> Map.of(field, keyvalue[1]))
                                .orElse(new HashMap<>());
                    })
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public boolean isValid() {
            return elements.keySet().size() == Field.values().length ||
                    (elements.size() == Field.values().length - 1 && !elements.containsKey(Field.cid));
        }

        public enum Field {
            byr,
            iyr,
            eyr,
            hgt,
            hcl,
            ecl,
            pid,
            cid
        }
    }

    public static void main (String[] args) {
        final List<String> input = Utils.loadFromFile("day4.txt").collect(Collectors.toList());
        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));
    }
}