package day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day11/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var map = input(path);

        var expansions = expansions(map);
        var rows = expansions.get(0);
        var cols = expansions.get(1);

        var galaxies = galaxies(map);

        int acc = 0;
        int n = 1;
        for (var galaxy : galaxies) {
            for (var galaxy2 : galaxies.stream().skip(n++).collect(Collectors.toList())) {
                acc += dist(galaxy, galaxy2, rows, cols);
            }
        }

        return acc;
    }

    static char[][] input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    static List<Set<Integer>> expansions(char[][] map) {
        var row = IntStream.range(0, map.length).boxed().collect(Collectors.toSet());
        var cols = IntStream.range(0, map[0].length).boxed().collect(Collectors.toSet());

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '#') {
                    row.remove(i);
                    cols.remove(j);
                }
            }

        }

        var list = new ArrayList<Set<Integer>>();
        list.add(row);
        list.add(cols);

        return list;
    }

    static List<int[]> galaxies(char[][] map) {
        var res = new ArrayList<int[]>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '#')
                    res.add(new int[]{i, j});
            }
        }

        return res;
    }

    static int dist(int[] a, int[] b, Set<Integer> rows, Set<Integer> cols) {
        var r0 = Math.min(a[0], b[0]);
        var r1 = Math.max(a[0], b[0]);
        var c0 = Math.min(a[1], b[1]);
        var c1 = Math.max(a[1], b[1]);

        int acc = 0;
        acc += r1 - r0;
        acc += rows.stream().filter(r -> r > r0 && r < r1).count();
        acc += c1 - c0;
        acc += cols.stream().filter(c -> c > c0 && c < c1).count();

        return acc;
    }
}
