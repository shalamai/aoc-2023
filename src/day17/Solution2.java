package day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


public class Solution2 {

    public static void main(String[] args) throws IOException {
        var res2 = part2("./src/day17/input.txt");
        System.out.println(res2);
    }

    static int part2(String path) throws IOException {
        var grid = input(path);

        var costs = new HashMap<Coord, Map<Direction, Integer>>();
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++)
                costs.put(new Coord(i, j), new HashMap());

        Queue<Cell> q = new LinkedList<>();

        var init = new Cell(new Coord(0, 0), new Direction(FROM.left, 0));
        q.add(init);
        costs.get(init.coord).put(init.dir, 0);

        while (!q.isEmpty()) {
            var cell = q.poll();

            for (Cell next : cell.neighbours(grid.length - 1, grid[0].length - 1)) {
                var newCost = costs.get(cell.coord).get(cell.dir) + grid[next.coord.i][next.coord.j];

                var betterCostExists = costs.get(next.coord).entrySet().stream()
                        .anyMatch(c -> c.getKey().from == next.dir.from && c.getKey().consecutive == next.dir.consecutive && c.getValue() <= newCost);

                if (!betterCostExists) {
                    costs.get(next.coord).put(next.dir, newCost);
                    q.add(next);
                }
            }
        }

        return costs.get(new Coord(grid.length - 1, grid[0].length - 1))
                .entrySet()
                .stream()
                .filter(e -> e.getKey().consecutive >= 4)
                .map(Map.Entry::getValue)
                .min(Integer::compareTo)
                .get();
    }

    static int[][] input(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .map(a -> a.chars().map(Character::getNumericValue).toArray())
                .toArray(int[][]::new);
    }

    record Cell(Coord coord, Direction dir) {
        List<Cell> neighbours(int maxI, int maxJ) {
            return Stream.of(up(), down(maxI), left(), right(maxJ))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        Optional<Cell> up() {
            if (coord.i == 0 || dir.from == FROM.top || (dir.from == FROM.bottom && dir.consecutive == 10) || (dir.from != FROM.bottom && dir.consecutive < 4))
                return Optional.empty();
            else
                return Optional.of(new Cell(new Coord(coord.i - 1, coord.j), new Direction(FROM.bottom, dir.from == FROM.bottom ? dir.consecutive + 1 : 1)));
        }

        Optional<Cell> down(int maxI) {
            if (coord.i == maxI || dir.from == FROM.bottom || (dir.from == FROM.top && dir.consecutive == 10) || (dir.from != FROM.top && dir.consecutive < 4))
                return Optional.empty();
            else
                return Optional.of(new Cell(new Coord(coord.i + 1, coord.j), new Direction(FROM.top, dir.from == FROM.top ? dir.consecutive + 1 : 1)));
        }

        Optional<Cell> left() {
            if (coord.j == 0 || dir.from == FROM.left || (dir.from == FROM.right && dir.consecutive == 10) || (dir.from != FROM.right && dir.consecutive < 4))
                return Optional.empty();
            else
                return Optional.of(new Cell(new Coord(coord.i, coord.j - 1), new Direction(FROM.right, dir.from == FROM.right ? dir.consecutive + 1 : 1)));
        }

        Optional<Cell> right(int maxJ) {
            if (coord.j == maxJ || dir.from == FROM.right || (dir.from == FROM.left && dir.consecutive == 10) || (dir.from != FROM.left && dir.consecutive < 4))
                return Optional.empty();
            else
                return Optional.of(new Cell(new Coord(coord.i, coord.j + 1), new Direction(FROM.left, dir.from == FROM.left ? dir.consecutive + 1 : 1)));
        }
    }

    record Coord(int i, int j) {
    }

    record Direction(FROM from, int consecutive) {
    }

    enum FROM {
        top, bottom, left, right
    }
}