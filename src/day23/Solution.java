package day23;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Solution {
    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day23/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day23/input.txt");
        System.out.println(res2);
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
                    .toList();

            next = next.size() > 1 ? next.stream().map(n -> n.appendToPath(randomString())).toList() : next;

            q.addAll(next);
        }

        return max;
    }

    static int part2(String path) throws IOException {
        var grid = input(path);
        var graph = buildGraph(grid);
        var finish = new Coord(grid.length - 1, grid[0].length - 2);

        var max = 0;
        Queue<Answer> q = new LinkedList<>();
        q.add(new Answer(List.of(new Coord(0, 1)), 0));

        while (!q.isEmpty()) {
            var curr = q.poll();
            var last = curr.path.get(curr.path.size() - 1);

            if (last.equals(finish)) max = Math.max(curr.steps, max);
            else q.addAll(graph.get(last).entrySet().stream()
                    .filter(e -> !curr.path.contains(e.getKey()))
                    .map(curr::advance)
                    .toList());
        }

        return max;
    }

    static Map<Coord, Map<Coord, Integer>> buildGraph(char[][] grid) {
        var res = new HashMap<Coord, Map<Coord, Integer>>();

        Queue<Edge> q = new LinkedList<>();
        q.add(new Edge(new Coord(0, 1), new Coord(0, 1), new Coord(0, 1), 0));
        var visited = new ArrayList<Coord>();

        while (!q.isEmpty()) {
            var e = q.poll();

            var next = e.curr.next().stream()
                    .filter(n -> n.i > 0 && n.i < grid.length && n.j > 0 && n.j < grid[0].length
                            && (grid[n.i][n.j] != '#') && !e.prev.equals(n))
                    .toList();

            if (next.size() == 1) q.add(new Edge(e.from, e.curr, next.get(0), e.steps + 1));
            else if (next.size() > 1 || e.curr.equals(new Coord(grid.length - 1, grid[0].length - 2))) {
                q.addAll(next.stream()
                        .filter(n -> !visited.contains(n))
                        .map(n -> new Edge(e.curr, e.curr, n, 1))
                        .toList());
                visited.addAll(next);
                if (!res.containsKey(e.from)) res.put(e.from, new HashMap<>());
                res.get(e.from).put(e.curr, e.steps);
            }
        }

        return res;
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

    record Edge(Coord from, Coord prev, Coord curr, int steps) {
    }

    record Coord(int i, int j) {
        List<Coord> next() {
            return List.of(
                    new Coord(i + 1, j),
                    new Coord(i - 1, j),
                    new Coord(i, j + 1),
                    new Coord(i, j - 1)
            );
        }
    }

    record Answer(List<Coord> path, int steps) {
        Answer advance(Map.Entry<Coord, Integer> next) {
            var path2 = new ArrayList<>(path);
            path2.add(next.getKey());
            return new Answer(path2, this.steps + next.getValue());
        }
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
