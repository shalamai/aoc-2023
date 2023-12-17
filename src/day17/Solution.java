package day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day17/input.txt");
        System.out.println(res1);
    }

    // todo: starting position = 0
    static int part1(String path) throws IOException {
        var grid = input(path);
        var costs = new int[grid.length][grid[0].length];
        var visited = new boolean[grid.length][grid[0].length];
        var trails = new Cell[grid.length][grid[0].length];
        Queue<Cell> q = new LinkedList<>();

        q.add(new Cell(0, 0, FROM.left, 0, new ArrayList<>()));
        visited[0][0] = true;

        while (!q.isEmpty()) {
            var cell = q.poll();

            for (Cell next : cell.neighbours(grid.length - 1, grid[0].length - 1)) {
                var newCost = costs[cell.i][cell.j] + grid[next.i][next.j];

                if (!visited[next.i][next.j] || costs[next.i][next.j] > newCost) {
                    costs[next.i][next.j] = newCost;
                    visited[next.i][next.j] = true;
                    trails[next.i][next.j] = next;
                    q.add(next);
                }
            }
        }

//        Arrays.stream(costs).forEach(a -> System.out.println(Arrays.toString(a)));
//        trails[costs.length - 1][costs[0].length - 1].trail.forEach(System.out::println);

        return costs[costs.length - 1][costs[0].length - 1];
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

    enum FROM {
        top, bottom, left, right
    }
}