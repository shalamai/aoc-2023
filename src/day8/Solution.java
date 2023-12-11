package day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        int res1 = part1("./src/day8/input.txt");
        System.out.println(res1);

        long res2 = part2("./src/day8/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        var instructions = instructions(path);
        var map = map(path);

        int steps = 0, instrIdx = 0;
        String node = "AAA";

        while (!node.equals("ZZZ")) {
            steps++;
            node = instructions.charAt(instrIdx) == 'L'
                    ? map.get(node)[0]
                    : map.get(node)[1];
            instrIdx = instrIdx < instructions.length() - 1
                    ? instrIdx + 1
                    : 0;
        }

        return steps;
    }

    static long part2(String path) throws IOException {
        var instructions = instructions(path);
        var map = map(path);

        return map.keySet().stream()
                .filter(n -> n.endsWith("A"))
                .map(n -> stepsToZ(n, instructions, map))
                .reduce(Solution::lcm)
                .get();
    }


    static String instructions(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).get(0);
    }

    static Map<String, String[]> map(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .skip(2)
                .collect(Collectors.toMap(l -> l.substring(0, 3), l -> new String[]{
                        l.substring(7, 10), l.substring(12, 15)
                }));
    }

    static long stepsToZ(String node, String instructions, Map<String, String[]> map) {
        var steps = 0L;
        var instrIdx = 0;

        while (!node.endsWith("Z")) {
            steps++;
            node = instructions.charAt(instrIdx) == 'L'
                    ? map.get(node)[0]
                    : map.get(node)[1];
            instrIdx = instrIdx < instructions.length() - 1
                    ? instrIdx + 1
                    : 0;
        }

        return steps;
    }

    static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        }
        var absNumber1 = Math.abs(number1);
        var absNumber2 = Math.abs(number2);
        var absHigherNumber = Math.max(absNumber1, absNumber2);
        var absLowerNumber = Math.min(absNumber1, absNumber2);
        var lcm = absHigherNumber;
        while (lcm % absLowerNumber != 0) {
            lcm += absHigherNumber;
        }
        return lcm;
    }
}
