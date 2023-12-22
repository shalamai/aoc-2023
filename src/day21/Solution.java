package day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day21/input.txt", 64);
        System.out.println(res1);
    }

    static int part1(String path, int steps) throws IOException {
        var grid = input(path);
        var start = start(grid);
        bfs(grid, start[0], start[1]);
        return accessible(grid, steps);
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

    static int[] start(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 0) return new int[]{i, j};
            }
        }
        throw new Error("invalid grid");
    }

    static void bfs(int[][] grid, int i0, int j0) {
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{i0, j0, 0});
        grid[i0][j0] = -1;

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

    static int accessible(int[][] grid, int steps) {
        int acc = steps % 2 == 0 ? 1 : 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                var v = grid[i][j];
                if (v > 0 && v <= steps && steps % 2 == v % 2) acc++;
            }
        }

        return acc;
    }
}
