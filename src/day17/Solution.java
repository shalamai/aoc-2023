package day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

// refactor this shit
// skip if there is a cost with less value and less-or-equal conseq
// try to improve performance
// - before refactor: 3723529 ops, 9527 ms
// - after: 2110969 ops, 4759 ms
// use Cell(with val) instead of Cost
// hide costs array inside data structure. Cell has all fields needed to work with it
// rm trail

// code 2 part
// - adjust cell.next(min 4, max 10)
// - res filter (conseq >= 4)

public class Solution {

    public static void main(String[] args) throws IOException {
        var start = System.currentTimeMillis();
        var res1 = part1("./src/day17/input.txt");
        System.out.println("done in " + (System.currentTimeMillis() - start));
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var grid = input(path);
        var costs = new ArrayList<List<Map<Direction, Integer>>>();

        for (int i = 0; i < grid.length; i++) {
            costs.add(new ArrayList<>());
            for (int j = 0; j < grid[0].length; j++) {
                costs.get(i).add(new HashMap());
            }
        }

        Queue<Cell> q = new LinkedList<>();

        q.add(new Cell(0, 0, new Direction(FROM.left, 0), new ArrayList<>()));
        costs.get(0).get(0).put(new Direction(FROM.left, 0), 0);

        while (!q.isEmpty()) {
            var cell = q.poll();

            for (Cell next : cell.neighbours(grid.length - 1, grid[0].length - 1)) {
                var newCost = costs.get(cell.i).get(cell.j).get(cell.dir) + grid[next.i][next.j];

                if (costs.get(next.i).get(next.j).entrySet().stream().filter(c -> c.getKey().from == next.dir.from && c.getKey().consecutive <= next.dir.consecutive && c.getValue() <= newCost).findFirst().isEmpty()) {
                    costs.get(next.i).get(next.j).put(next.dir, newCost);
                    q.add(next);
                }
            }
        }

        return costs.get(costs.size() - 1).get(costs.get(0).size() - 1).values().stream().min(Integer::compareTo).get();
    }

    static int[][] input(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .map(a -> a.chars().map(Character::getNumericValue).toArray())
                .toArray(int[][]::new);
    }

    record Cell(int i, int j, Direction dir, List<Cell> trail) {
        List<Cell> neighbours(int maxI, int maxJ) {
            return Stream.of(up(), down(maxI), left(), right(maxJ))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        Optional<Cell> up() {
            if (i == 0 || dir.from == FROM.top || (dir.from == FROM.bottom && dir.consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i - 1, j, new Direction(FROM.bottom, dir.from == FROM.bottom ? dir.consecutive + 1 : 1), as));
            }
        }

        Optional<Cell> down(int maxI) {
            if (i == maxI || dir.from == FROM.bottom || (dir.from == FROM.top && dir.consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i + 1, j, new Direction(FROM.top, dir.from == FROM.top ? dir.consecutive + 1 : 1), as));
            }
        }

        Optional<Cell> left() {
            if (j == 0 || dir.from == FROM.left || (dir.from == FROM.right && dir.consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i, j - 1, new Direction(FROM.right, dir.from == FROM.right ? dir.consecutive + 1 : 1), as));
            }
        }

        Optional<Cell> right(int maxJ) {
            if (j == maxJ || dir.from == FROM.right || (dir.from == FROM.left && dir.consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i, j + 1, new Direction(FROM.left, dir.from == FROM.left ? dir.consecutive + 1 : 1), as));
            }
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "i=" + i +
                    ", j=" + j +
                    ", from=" + dir.from +
                    ", consecutive=" + dir.consecutive +
                    '}';
        }
    }

    record Direction(FROM from, int consecutive) {
    }

    enum FROM {
        top, bottom, left, right
    }
}