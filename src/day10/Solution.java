package day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day10/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day10/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        var map = map(path);
        var start = start(map);
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

    static int part2(String path) throws IOException {
        var map = map(path);
        var scaled = scale(map);
        var start = start(scaled);

        markLoop(scaled, start);
        markOutsideTiles(scaled);
        expandLoopBorders(scaled);

        return countInsideTiles(scaled) / 9;
    }

    static int[] start(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            var line = String.valueOf(map[i]);
            if (line.contains("S")) return new int[]{i, line.indexOf("S")};
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

    static char[][] scale(char[][] map) {
        var map2 = mapWithDots(map.length * 3, map[0].length * 3);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                var v = map[i][j];
                setValIncr(map2, i * 3, j * 3, v);
                if (List.of('S', '|', 'J', 'L').contains(v)) setValIncr(map2, i * 3 - 1, j * 3, '|');
                if (List.of('S', '|', 'F', '7').contains(v)) setValIncr(map2, i * 3 + 1, j * 3, '|');
                if (List.of('S', '-', 'J', '7').contains(v)) setValIncr(map2, i * 3, j * 3 - 1, '-');
                if (List.of('S', '-', 'L', 'F').contains(v)) setValIncr(map2, i * 3, j * 3 + 1, '-');
            }
        }

        return map2;
    }

    static void setVal(char[][] map, int i, int j, char v) {
        if (i >= 0 && i < map.length && j >= 0 && j < map[0].length && map[i][j] != '1') map[i][j] = v;
    }

    static void setValIncr(char[][] map, int i, int j, char v) {
        i++;
        j++;
        setVal(map, i, j, v);
    }

    static char[][] mapWithDots(int i, int j) {
        var map = new char[i][j];
        for (int k = 0; k < i; k++) {
            var arr = new char[j];
            Arrays.fill(arr, '.');
            map[k] = arr;
        }

        return map;
    }

    static void markLoop(char[][] map, int[] start) {
        var next = neighbours(map, start).stream()
                .filter(n -> neighbours(map, n).stream().anyMatch(n2 -> !Arrays.equals(n2, start)))
                .findFirst()
                .get();

        map[start[0]][start[1]] = '1';
        while (true) {
            var ns = neighbours(map, next);
            map[next[0]][next[1]] = '1';
            if (ns.isEmpty()) return;
            next = ns.get(0);
        }
    }

    static void markOutsideTiles(char[][] map) {
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{0, 0});

        while (!q.isEmpty()) {
            var a = q.poll();
            if (a[0] < 0 || a[0] >= map.length ||
                    a[1] < 0 || a[1] >= map[0].length ||
                    map[a[0]][a[1]] == '1' || map[a[0]][a[1]] == '2') continue;

            map[a[0]][a[1]] = '2';

            q.add(new int[]{a[0] + 1, a[1]});
            q.add(new int[]{a[0] - 1, a[1]});
            q.add(new int[]{a[0], a[1] - 1});
            q.add(new int[]{a[0], a[1] + 1});
        }
    }

    static void expandLoopBorders(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '1') {
                    setVal(map, i + 1, j - 1, '2');
                    setVal(map, i + 1, j, '2');
                    setVal(map, i + 1, j + 1, '2');
                    setVal(map, i - 1, j - 1, '2');
                    setVal(map, i - 1, j, '2');
                    setVal(map, i - 1, j + 1, '2');
                    setVal(map, i, j - 1, '2');
                    setVal(map, i, j + 1, '2');
                }
            }
        }
    }

    static int countInsideTiles(char[][] map) {
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != '1' && map[i][j] != '2') count++;
            }
        }

        return count;
    }
}
