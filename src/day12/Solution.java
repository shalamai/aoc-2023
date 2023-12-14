package day12;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day12/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var rs1 = records1(path);
        var rs2 = records2(path);

        int acc = 0;
        for (int i = 0; i < rs1.size(); i++) {
            acc += solve(rs1.get(i), rs2.get(i), false);
        }

        return acc;
    }

    static List<String> records1(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(l -> l.split(" ")[0])
                .collect(Collectors.toList());
    }

    static List<List<Integer>> records2(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(l -> Arrays.stream(l.split(" ")[1].split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    static int solve(CharSequence r1, List<Integer> r2, boolean breakNeeded) {
        if (r1.length() == 0) return r2.isEmpty() ? 1 : 0;
        if (r2.isEmpty()) return Pattern.matches(".*#.*", r1) ? 0 : 1;
        if (breakNeeded) return r1.charAt(0) == '#' ? 0 : solveDot(r1, r2);
        if (r1.charAt(0) == '.') return solveDot(r1, r2);
        if (r1.charAt(0) == '#') return solveHash(r1, r2);
        return solveDot(r1, r2) + solveHash(r1, r2);
    }

    static int solveDot(CharSequence r1, List<Integer> r2) {
        return solve(CharBuffer.wrap(r1, 1, r1.length()), r2, false);
    }

    static int solveHash(CharSequence r1, List<Integer> r2) {
        return Pattern.matches("^(#|\\?){" + r2.get(0) + "}.*", r1)
                ? solve(CharBuffer.wrap(r1, r2.get(0), r1.length()), r2.subList(1, r2.size()), true)
                : 0;
    }
}
