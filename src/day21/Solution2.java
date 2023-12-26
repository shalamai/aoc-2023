package day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// discoveries
// - there are 2 full-grid possible setups

// assumptions
// - free of obstacles border, horizontal/vertical way from the start
// - grid is a square, start is in the middle

public class Solution2 {
    public static void main(String[] args) throws IOException {
        var res2 = part2("./src/day21/input.txt", 26501365);
        System.out.println(res2);
    }
    static int[][] stepsFromBottom = null;
    static int[][] stepsFromCorner = null;
    static Map<Integer, Integer> plotsFromBottomCache = null;
    static Map<Integer, Integer> plotsFromCornerCache = null;

    static long part2(String path, int steps) throws IOException {
        var grid = input(path);
        var start = (grid.length - 1) / 2;
        grid[start][start] = -1;
        var fullGrids = fullGrids(grid, start, steps);

        long acc = fullGrids[0]; // initial grid
        for (int i = 0; i < 4; i++) {
            System.out.println("i - " + i);

            initState(grid, start);
            acc += goUpRight(grid.length, fullGrids, start, steps);
            rotate90(grid);
        }

        return acc;
    }

    static int[][] input(String path) throws IOException {
        var grid = Files.readAllLines(Path.of(path)).stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        var res = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '#') res[i][j] = -2;
                if (grid[i][j] == '.') res[i][j] = -1;
            }
        }

        return res;
    }

    static void initState(int[][] grid, int start) {
        stepsFromBottom = copy(grid);
        incrementStepsFrom(stepsFromBottom, new int[]{grid.length - 1, start});
        stepsFromCorner = copy(grid);
        incrementStepsFrom(stepsFromCorner, new int[]{grid.length - 1, 0});
        plotsFromBottomCache = new HashMap<>();
        plotsFromCornerCache = new HashMap<>();
    }

    static int[] fullGrids(int[][] grid, int start, int steps) {
        var doubleGrid = new int[grid.length][grid[0].length * 2];
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = 0; j < 2; j++) {
                int shift = grid[0].length * j;
                System.arraycopy(grid[i], 0, doubleGrid[i], shift, grid[0].length);
            }
        }

        incrementStepsFrom(doubleGrid, new int[]{start, start});

        return new int[]{
                countAccessiblePlots(doubleGrid, steps, new int[]{0, 0}, new int[]{grid.length - 1, grid[0].length - 1}),
                countAccessiblePlots(doubleGrid, steps, new int[]{0, grid[0].length}, new int[]{grid.length - 1, 2 * grid[0].length - 1})
        };
    }

    static void incrementStepsFrom(int[][] grid, int[] from) {
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{from[0], from[1], 0});

        while (!q.isEmpty()) {
            var coord = q.poll();
            var i = coord[0];
            var j = coord[1];
            var v = coord[2];
            if (i >= 0 && i < grid.length && j >= 0 && j < grid[0].length && grid[i][j] == -1) {
                grid[i][j] = coord[2];
                q.add(new int[]{i + 1, j, v + 1});
                q.add(new int[]{i - 1, j, v + 1});
                q.add(new int[]{i, j + 1, v + 1});
                q.add(new int[]{i, j - 1, v + 1});
            }
        }
    }

    static int accessiblePlotsFromBottom(int steps) {
        if (plotsFromBottomCache.containsKey(steps)) return plotsFromBottomCache.get(steps);
        var res = countAccessiblePlots(stepsFromBottom, steps);
        plotsFromBottomCache.put(steps, res);
        return res;
    }

    static int accessiblePlotsFromCorner(int steps) {
        if (plotsFromCornerCache.containsKey(steps)) return plotsFromCornerCache.get(steps);
        var res = countAccessiblePlots(stepsFromCorner, steps);
        plotsFromCornerCache.put(steps, res);
        return res;
    }

    static int countAccessiblePlots(int[][] grid, int steps) {
        return countAccessiblePlots(grid, steps, new int[]{0, 0}, new int[]{grid.length - 1, grid[0].length - 1});
    }

    static int countAccessiblePlots(int[][] grid, int steps, int[] from, int[] to) {
        int acc = 0;

        for (int i = from[0]; i <= to[0]; i++) {
            for (int j = from[1]; j <= to[1]; j++) {
                var v = grid[i][j];
                if ((v == 0 && steps % 2 == 0) || (v > 0 && v <= steps && steps % 2 == v % 2)) acc++;
            }
        }

        return acc;
    }

    static void swap(int[] arr) {
        int tmp = arr[0];
        arr[0] = arr[1];
        arr[1] = tmp;
    }

    static int[][] copy(int[][] arr) {
        int[][] res = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = Arrays.copyOf(arr[i], arr[0].length);
        }

        return res;
    }

    static long goUpRight(int gridLength, int[] fullGrids, int start, int steps) {
        fullGrids = Arrays.copyOf(fullGrids, fullGrids.length);

        var acc = 0L;
        var bottomMid = start + 1;
        while (bottomMid <= steps) {
            var topLeft = bottomMid + start + gridLength - 1;
            if (topLeft <= steps) { // full grid
                swap(fullGrids);
                acc += fullGrids[0];
            } else { // partial grid
                acc += accessiblePlotsFromBottom(steps - bottomMid);
            }
            acc += goRight(gridLength, fullGrids, steps, bottomMid + (gridLength - start));
            bottomMid += gridLength;
        }

        return acc;
    }

    static long goRight(int gridLength, int[] fullGrids, int steps, int leftDown) {
        fullGrids = Arrays.copyOf(fullGrids, fullGrids.length);

        var acc = 0L;
        while (leftDown <= steps) {
            var topRight = leftDown + (gridLength - 1) + (gridLength - 1);
            if (topRight <= steps) { // full grid
                swap(fullGrids);
                acc += fullGrids[0];
            } else { // partial grid
                acc += accessiblePlotsFromCorner(steps - leftDown);
            }
            leftDown += gridLength;
        }

        return acc;
    }


    static void rotate90(int[][] matrix) {
        if (matrix.length != matrix[0].length) throw new Error("invalid grid");
        int tmp, max;
        for (int depth = 0; depth < matrix.length / 2; depth++) {
            for (int offset = 0; offset < matrix.length - 2 * depth - 1; offset++) {
                max = matrix.length - 1 - depth;
                tmp = matrix[depth][depth + offset];
                matrix[depth][depth + offset] = matrix[max - offset][depth];
                matrix[max - offset][depth] = matrix[max][max - offset];
                matrix[max][max - offset] = matrix[depth + offset][max];
                matrix[depth + offset][max] = tmp;
            }
        }
    }
}
