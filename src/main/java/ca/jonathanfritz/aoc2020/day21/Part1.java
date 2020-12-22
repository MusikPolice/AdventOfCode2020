package ca.jonathanfritz.aoc2020.day21;

import ca.jonathanfritz.aoc2020.Utils;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part1 {

    private long solve(List<String> input) {
        // map key is ingredient, value is number of times it appears in the input
        final Map<String, Integer> ingredientCounts = new HashMap<>();

        // key is an allergen, value is set of ingredients that it might be in
        final Map<String, Set<String>> allergensIngredients = new HashMap<>();

        for (String line : input) {
            final int parenthesisStart = line.indexOf("(");
            final List<String> ingredients;
            if (parenthesisStart != -1) {
                ingredients = Arrays.stream(line.substring(0, parenthesisStart - 1).split(" "))
                        .map(String::trim)
                        .filter(s -> s.length() > 0)
                        .collect(Collectors.toList());
            } else {
                ingredients = Arrays.stream(line.split(" "))
                        .map(String::trim)
                        .filter(s -> s.length() > 0)
                        .collect(Collectors.toList());
            }

            // count number of times each ingredient appears
            for (String ingredient : ingredients) {
                if (ingredientCounts.containsKey(ingredient)) {
                    ingredientCounts.put(ingredient, ingredientCounts.get(ingredient) + 1);
                } else {
                    ingredientCounts.put(ingredient, 1);
                }
            }

            // figure out allergens
            final int allergensStart = line.indexOf("(contains ") + 10;
            if (allergensStart > 9) {
                final List<String> allergens = Arrays.stream(line.substring(allergensStart).split(" "))
                        .map(String::trim)
                        .map(s -> {
                            if (s.endsWith(")")) {
                                return s.substring(0, s.length() - 1);
                            } else if (s.endsWith(",")) {
                                return s.substring(0, s.length() - 1);
                            } else {
                                return s;
                            }
                        })
                        .filter(s -> s.length() > 0)
                        .collect(Collectors.toList());

                for (String allergen : allergens) {
                    if (allergensIngredients.containsKey(allergen)) {
                        // process of elimination
                        final Set<String> potentialIngredients = allergensIngredients.get(allergen)
                                .stream()
                                .filter(ingredients::contains)
                                .collect(Collectors.toSet());
                        allergensIngredients.put(allergen, potentialIngredients);
                    } else {
                        // the allergen could be in any of the ingredients
                        allergensIngredients.put(allergen, new HashSet<>(ingredients));
                    }
                }
            }
        }

        // process of elimination - by the end, all allergens should be positively linked with exactly one ingredient
        while (!allergensIngredients.values().stream().allMatch(i -> i.size() == 1)) {
            final List<String> ingredientsToRemove = allergensIngredients.values().stream()
                    .filter(i -> i.size() == 1)
                    .flatMap((Function<Set<String>, Stream<String>>) Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
            for (Map.Entry<String, Set<String>> entry : allergensIngredients.entrySet()) {
                if (entry.getValue().size() > 1) {
                    entry.getValue().removeAll(ingredientsToRemove);
                }
            }
        }

        final List<String> ingredientsWithAllergens = allergensIngredients.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        long occurrences = 0;
        for (Map.Entry<String, Integer> entry : ingredientCounts.entrySet()) {
            if (!ingredientsWithAllergens.contains(entry.getKey())) {
                occurrences += entry.getValue();
            }
        }

        return occurrences;
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final List<String> input = Utils.loadFromFile("day21.txt")
                .collect(Collectors.toList());

        final Part1 part1 = new Part1();
        System.out.println(part1.solve(input));

        final long durationMs = System.currentTimeMillis() - startTime;
        final Duration duration = Duration.ofMillis(durationMs);
        System.out.println("Runtime: " + duration.toString());
    }
}
