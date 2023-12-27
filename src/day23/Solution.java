package day23;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Solution {
    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day23/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var grid = input(path);
        var visited = new ArrayList<List<Set<String>>>();
        for (int i = 0; i < grid.length; i++) {
            var list = new ArrayList<Set<String>>();
            for (int j = 0; j < grid[0].length; j++) {
                list.add(new HashSet<>());
            }
            visited.add(list);
        }

        int max = 0;

        Queue<Path> q = new LinkedList<>();
        q.add(new Path(randomString(), 0, 1, 0, '-'));

        while (!q.isEmpty()) {
            var p = q.poll();
            if (p.i == grid.length - 1 && p.j == grid[0].length - 2 && p.steps > max) max = p.steps;

            visited.get(p.i).get(p.j).add(p.pathId);

            var next = p.next().stream()
                    .filter(n -> n.i > 0 && n.i < grid.length && n.j > 0 && n.j < grid[0].length &&
                            (visited.get(n.i).get(n.j).stream().noneMatch(n.pathId::startsWith)) &&
                            (grid[n.i][n.j] == '.' || (grid[n.i][n.j] == n.dir)))
                    .map(n -> n.appendToPath(randomString()))
                    .toList();

            q.addAll(next);
        }

        return max;
    }

    static char[][] input(String path) throws IOException {
        return Files.readAllLines(java.nio.file.Path.of(path))
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    static String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 2;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    record Path(String pathId, int i, int j, int steps, char dir) {
        List<Path> next() {
            return List.of(
                    new Path(pathId, i + 1, j, steps + 1, 'v'),
                    new Path(pathId, i - 1, j, steps + 1, '-'),
                    new Path(pathId, i, j + 1, steps + 1, '>'),
                    new Path(pathId, i, j - 1, steps + 1, '-')
            );
        }

        Path appendToPath(String a) {
            return new Path(pathId + a, i, j, steps, dir);
        }
    }
}
