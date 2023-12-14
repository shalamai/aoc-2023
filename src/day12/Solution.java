package day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day12/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day12/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        var rs1 = records1(path);
        var rs2 = records2(path);

        int acc = 0;
        for (int i = 0; i < rs1.size(); i++) {
            acc += solve(rs1.get(i), 0, rs2.get(i), 0, false, new HashMap<>());
        }

        return acc;
    }

    static long part2(String path) throws IOException {
        var rs1 = records1(path).stream()
                .map(a -> (a + "?").repeat(5))
                .map(a -> a.substring(0, a.length() - 1))
                .collect(Collectors.toList());

        var rs2 = records2(path).stream()
                .map(as -> Collections.nCopies(5, as)
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        long acc = 0;
        for (int i = 0; i < rs1.size(); i++) {
            acc += solve(rs1.get(i), 0, rs2.get(i), 0, false, new HashMap<>());
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

    static long solveThroughCache(String r1, int r1Start, List<Integer> r2, int r2Start, boolean breakNeeded, Map<String, Long> cache) {
        var key = r1Start + ":" + r2Start + ":" + breakNeeded;
        if (cache.containsKey(key)) return cache.get(key);
        var r = solve(r1, r1Start, r2, r2Start, breakNeeded, cache);
        cache.put(key, r);
        return r;
    }

    static long solve(String r1, int r1Start, List<Integer> r2, int r2Start, boolean breakNeeded, Map<String, Long> cache) {
        if (r1Start == r1.length()) return r2Start == r2.size() ? 1 : 0;
        if (r2Start == r2.size()) return r1.substring(r1Start).matches(".*#.*") ? 0 : 1;
        if (breakNeeded) return r1.charAt(r1Start) == '#' ? 0 : solveDot(r1, r1Start, r2, r2Start, cache);
        if (r1.charAt(r1Start) == '.') return solveDot(r1, r1Start, r2, r2Start, cache);
        if (r1.charAt(r1Start) == '#') return solveHash(r1, r1Start, r2, r2Start, cache);
        return solveDot(r1, r1Start, r2, r2Start, cache) + solveHash(r1, r1Start, r2, r2Start, cache);
    }

    static long solveDot(String r1, int r1Start, List<Integer> r2, int r2Start, Map<String, Long> cache) {
        return solveThroughCache(r1, r1Start + 1, r2, r2Start, false, cache);
    }

    static long solveHash(String r1, int r1Start, List<Integer> r2, int r2Start, Map<String, Long> cache) {
        return r1.substring(r1Start).matches("^(#|\\?){" + r2.get(r2Start) + "}.*")
                ? solveThroughCache(r1, r1Start + r2.get(r2Start), r2, r2Start + 1, true, cache)
                : 0;
    }
}
