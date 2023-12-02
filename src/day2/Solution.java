package day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static void main(String[] args) throws IOException {
        System.out.println(part1());
        System.out.println(part2());
    }

    static Map<String, Integer> limits = Map.of("red", 12, "green", 13, "blue", 14);

    static int part1() throws IOException {
        var lines = Files.readAllLines(Path.of("./src/day2/input.txt"));

        int acc = 0;
        for (String line : lines) {
            var res = line.split(":");
            if (isGameOk(res[1])) {
                acc += Integer.parseInt(res[0].substring(5));
            }
        }

        return acc;
    }

    static boolean isGameOk(String set) {
        for (String subset : set.split(";")) {
            for (String cubes : subset.split(",")) {
                String[] colorN = cubes.split(" ");
                if (Integer.parseInt(colorN[1]) > limits.get(colorN[2])) {
                    return false;
                }
            }
        }

        return true;
    }

    static int part2() throws IOException {
        var lines = Files.readAllLines(Path.of("./src/day2/input.txt"));

        int acc = 0;
        for (String line : lines) {
            acc += power(line.split(":")[1]);
        }

        return acc;
    }

    static int power(String set) {
        var max = new HashMap<>(Map.of("red", 0, "green", 0, "blue", 0));
        for (String subset : set.split(";")) {
            for (String cubes : subset.split(",")) {
                String[] colorN = cubes.split(" ");
                var n = Integer.parseInt(colorN[1]);
                if (n > max.get(colorN[2])) {
                    max.put(colorN[2], n);
                }
            }
        }

        return max.values().stream().reduce(1, (a, b) -> a * b);
    }
}
