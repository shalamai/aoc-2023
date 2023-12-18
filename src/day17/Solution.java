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
        var costs = new ArrayList<List<Map<Integer, Cost>>>();

        for (int i = 0; i < grid.length; i++) {
            costs.add(new ArrayList<>());
            for (int j = 0; j < grid[0].length; j++) {
                costs.get(i).add(new HashMap());
            }
        }

        Queue<Cell> q = new LinkedList<>();

        q.add(new Cell(0, 0, FROM.left, 0, new ArrayList<>()));
        var init = new Cost(FROM.left, 0, 0);
        costs.get(0).get(0).put(init.hashCode(), init);

        while (!q.isEmpty()) {
            var cell = q.poll();

            for (Cell next : cell.neighbours(grid.length - 1, grid[0].length - 1)) {
                var newCost = costs.get(cell.i).get(cell.j).get(new Cost(cell.from, cell.consecutive, 0).hashCode()).val + grid[next.i][next.j];

                if (costs.get(next.i).get(next.j).values().stream().filter(c -> c.from == next.from && c.consecutive <= next.consecutive && c.val <= newCost).findFirst().isEmpty()) {
                    var nc = new Cost(next.from, next.consecutive, newCost);
                    costs.get(next.i).get(next.j).put(nc.hashCode(), nc);
                    q.add(next);
                }
            }
        }

        return costs.get(costs.size() - 1).get(costs.get(0).size() - 1).values().stream().map(c -> c.val).min(Integer::compareTo).get();
    }

    static int[][] input(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .map(a -> a.chars().map(Character::getNumericValue).toArray())
                .toArray(int[][]::new);
    }

    record Cell(int i, int j, FROM from, int consecutive, List<Cell> trail) {
        List<Cell> neighbours(int maxI, int maxJ) {
            return Stream.of(up(), down(maxI), left(), right(maxJ))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        Optional<Cell> up() {
            if (i == 0 || from == FROM.top || (from == FROM.bottom && consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i - 1, j, FROM.bottom, from == FROM.bottom ? consecutive + 1 : 1, as));
            }
        }

        Optional<Cell> down(int maxI) {
            if (i == maxI || from == FROM.bottom || (from == FROM.top && consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i + 1, j, FROM.top, from == FROM.top ? consecutive + 1 : 1, as));
            }
        }

        Optional<Cell> left() {
            if (j == 0 || from == FROM.left || (from == FROM.right && consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i, j - 1, FROM.right, from == FROM.right ? consecutive + 1 : 1, as));
            }
        }

        Optional<Cell> right(int maxJ) {
            if (j == maxJ || from == FROM.right || (from == FROM.left && consecutive == 3)) return Optional.empty();
            else {
                var as = new ArrayList<>(trail);
                as.add(this);
                return Optional.of(new Cell(i, j + 1, FROM.left, from == FROM.left ? consecutive + 1 : 1, as));
            }
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "i=" + i +
                    ", j=" + j +
                    ", from=" + from +
                    ", consecutive=" + consecutive +
                    '}';
        }
    }

    record Cost(FROM from, int consecutive, int val) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cost cost = (Cost) o;
            return consecutive == cost.consecutive && from == cost.from;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, consecutive);
        }
    }

    enum FROM {
        top, bottom, left, right
    }
}