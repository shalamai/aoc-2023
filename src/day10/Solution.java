package day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day10/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var start = start(path);
        var map = map(path);
        var neighbours = neighbours(map, start);
        if (neighbours.size() != 2) throw new Error("invalid neighbours");

        int[] prev1 = start;
        int[] prev2 = start;

        int[] w1 = neighbours.get(0);
        int[] w2 = neighbours.get(1);

        int steps = 1;
        while (!Arrays.equals(w1, w2)) {
            var tmp1 = w1;
            var tmp2 = w2;

            w1 = next(map, prev1, w1);
            w2 = next(map, prev2, w2);

            prev1 = tmp1;
            prev2 = tmp2;
            steps++;
        }

        return steps;
    }

    static int[] start(String path) throws IOException {
        var lines = Files.readAllLines(Path.of(path));
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("S")) return new int[]{i, lines.get(i).indexOf("S")};
        }
        return new int[]{};
    }

    static char[][] map(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().map(String::toCharArray).toArray(char[][]::new);
    }

    static List<int[]> neighbours(char[][] map, int[] n) {
        var dirs = new HashMap<int[], List<Character>>();

        var v = map[n[0]][n[1]];
        if (List.of('S', '|', 'J', 'L').contains(v)) dirs.put(new int[]{n[0] - 1, n[1]}, List.of('|', '7', 'F'));
        if (List.of('S', '|', 'F', '7').contains(v)) dirs.put(new int[]{n[0] + 1, n[1]}, List.of('|', 'L', 'J'));
        if (List.of('S', '-', 'J', '7').contains(v)) dirs.put(new int[]{n[0], n[1] - 1}, List.of('-', 'L', 'F'));
        if (List.of('S', '-', 'L', 'F').contains(v)) dirs.put(new int[]{n[0], n[1] + 1}, List.of('-', 'J', '7'));

        return dirs.keySet().stream()
                .filter(d -> d[0] >= 0 && d[0] < map.length && d[1] >= 0 && d[1] < map[0].length)
                .filter(d -> dirs.get(d).contains(map[d[0]][d[1]]))
                .collect(Collectors.toList());
    }

    static int[] next(char[][] map, int[] prev, int[] n) {
        var res = neighbours(map, n).stream()
                .filter(neighbour -> !Arrays.equals(neighbour, prev))
                .findFirst()
                .get();

        return res;
    }
}
