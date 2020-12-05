package ca.jonathanfritz.aoc2020.day4;

import ca.jonathanfritz.aoc2020.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Part2 {

    private static final List<String> validEyeColours = Arrays.asList("amb", "blu", "brn", "gry", "grn", "hzl", "oth");

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
            try {
                final boolean hasAllFields = elements.keySet().size() == Field.values().length;
                final boolean hasAllFieldsExceptCid = elements.size() == Field.values().length - 1 && !elements.containsKey(Field.cid);
                if (!hasAllFields && !hasAllFieldsExceptCid) {
                    return false;
                }

                final int birthYear = Integer.parseInt(elements.get(Field.byr));
                if (birthYear < 1920 || birthYear > 2002) {
                    return false;
                }

                final int issueYear = Integer.parseInt(elements.get(Field.iyr));
                if (issueYear < 2010 || issueYear > 2020) {
                    return false;
                }

                final int expirationYear = Integer.parseInt(elements.get(Field.eyr));
                if (expirationYear < 2020 || expirationYear > 2030) {
                    return false;
                }

                final String height = elements.get(Field.hgt);
                if (height.endsWith("cm")) {
                    final int cms = Integer.parseInt(height.replace("cm", ""));
                    if (cms < 150 || cms > 193) {
                        return false;
                    }
                } else if (height.endsWith("in")) {
                    final int inches = Integer.parseInt(height.replace("in", ""));
                    if (inches < 59 || inches > 76) {
                        return false;
                    }
                } else {
                    return false;
                }

                final String hairColour = elements.get(Field.hcl);
                if (!hairColour.startsWith("#") || hairColour.length() != 7) {
                    return false;
                }
                Long.parseLong(hairColour.replace("#", ""), 16);

                final String eyeColour = elements.get(Field.ecl);
                if (!validEyeColours.contains(eyeColour)) {
                    return false;
                }

                final String passportNumber = elements.get(Field.pid);
                if (passportNumber.length() != 9) {
                    return false;
                }
                Long.parseLong(passportNumber);

                return true;

            } catch (NumberFormatException e) {
                return false;
            }
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
        final Part2 part2 = new Part2();
        System.out.println(part2.solve(input));
    }
}