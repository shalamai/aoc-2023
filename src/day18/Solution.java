package day18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day18/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var instructions = input(path);
        var border = border(instructions);
        var size = borderSize(border);
        var gridI = size[1] - size[0] + 1 + 2;
        var gridJ = size[3] - size[2] + 1 + 2;
        var grid = new int[gridI][gridJ];

        for (int[] coord : border) grid[coord[0] - size[0] + 1][coord[1] - size[2] + 1] = 1;
        fillExternalSpace(grid);

        return internalSpace(grid);
    }

    static List<String[]> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().map(line -> line.split(" ")).toList();
    }

    static List<int[]> border(List<String[]> instructions) {
        var res = new ArrayList<int[]>();
        var cur = new int[]{0, 0};
        res.add(cur);
        for (String[] instr : instructions) {
            var cur2 = cur;
            var next = switch (instr[0]) {
                case "U" ->
                        IntStream.rangeClosed(1, Integer.parseInt(instr[1])).boxed().map(i -> new int[]{cur2[0] - i, cur2[1]}).toList();
                case "D" ->
                        IntStream.rangeClosed(1, Integer.parseInt(instr[1])).boxed().map(i -> new int[]{cur2[0] + i, cur2[1]}).toList();
                case "L" ->
                        IntStream.rangeClosed(1, Integer.parseInt(instr[1])).boxed().map(i -> new int[]{cur2[0], cur2[1] - i}).toList();
                case "R" ->
                        IntStream.rangeClosed(1, Integer.parseInt(instr[1])).boxed().map(i -> new int[]{cur2[0], cur2[1] + i}).toList();
                default -> throw new Error("unknown direction");
            };
            res.addAll(next);
            cur = next.get(next.size() - 1);
        }

        return res;
    }

    static int[] borderSize(List<int[]> border) {
        return new int[]{
                border.stream().map(a -> a[0]).min(Integer::compareTo).get(),
                border.stream().map(a -> a[0]).max(Integer::compareTo).get(),
                border.stream().map(a -> a[1]).min(Integer::compareTo).get(),
                border.stream().map(a -> a[1]).max(Integer::compareTo).get()
        };
    }

    static void fillExternalSpace(int[][] grid) {
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{0, 0});

        while (!q.isEmpty()) {
            var coord = q.poll();
            var i = coord[0];
            var j = coord[1];

            if (grid[i][j] == 2 || grid[i][j] == 1) continue;

            grid[i][j] = 2;
            if (i > 0) q.add(new int[]{i - 1, j});
            if (i < grid.length - 1) q.add(new int[]{i + 1, j});
            if (j > 0) q.add(new int[]{i, j - 1});
            if (j < grid[0].length - 1) q.add(new int[]{i, j + 1});
        }
    }

    static int internalSpace(int[][] grid) {
        int acc = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != 2) acc++;
            }
        }

        return acc;
    }
}
