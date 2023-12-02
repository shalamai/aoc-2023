package day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {
    public static void main(String[] args) throws IOException {
        var res = part1();
        System.out.println(res);

        var res2 = part2();
        System.out.println(res2);
    }

    static int part1() throws IOException {
        var lines = Files.readAllLines(Path.of("./src/day1/input.txt"));
        var acc = 0;
        var first = 0;
        var last = 0;
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                if (Character.isDigit(line.charAt(i))) {
                    first = Character.getNumericValue(line.charAt(i));
                    break;
                }
            }

            for (int i = line.length() - 1; i >= 0; i--) {
                if (Character.isDigit(line.charAt(i))) {
                    last = Character.getNumericValue(line.charAt(i));
                    break;
                }
            }

            acc += 10 * first + last;
        }

        return acc;
    }


    static Map<String, Integer> digits = Map.of(
            "one", 1,
            "two", 2,
            "three", 3,
            "four", 4,
            "five", 5,
            "six", 6,
            "seven", 7,
            "eight", 8,
            "nine", 9
    );

    static int part2() throws IOException {
        var lines = Files.readAllLines(Path.of("./src/day1/input.txt"));
        var acc = 0;

        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                var res = getDigitIfPresent(line, i);
                if (res.isPresent()) {
                    acc += 10 * res.get();
                    break;
                }
            }

            for (int i = line.length() - 1; i >= 0; i--) {
                var res = getDigitIfPresent(line, i);
                if (res.isPresent()) {
                    acc += res.get();
                    break;
                }
            }
        }

        return acc;
    }

    static Optional<Integer> getDigitIfPresent(String line, int i) {
        if (Character.isDigit(line.charAt(i))) {
            return Optional.of(Character.getNumericValue(line.charAt(i)));
        }

        for (String digit : digits.keySet()) {
            if (line.regionMatches(i, digit, 0, digit.length())) {
                return Optional.of(digits.get(digit));
            }
        }

        return Optional.empty();
    }
}
