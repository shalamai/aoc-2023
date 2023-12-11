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
}
