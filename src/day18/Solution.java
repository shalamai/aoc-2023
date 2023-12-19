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
        var grid = new long[(int) gridI][(int) gridJ];

        for (long[] coord : border) grid[(int) (coord[0] - size[0] + 1)][(int) (coord[1] - size[2] + 1)] = 1;
        fillExternalSpace(grid);

        return internalSpace(grid);
    }

    static List<Instruction> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().map(line -> {
            var parts = line.split(" ");
            return new Instruction(parts[0], Integer.parseInt(parts[1]));
        }).toList();
    }

    static List<long[]> border(List<Instruction> instructions) {
        var res = new ArrayList<long[]>();
        var cur = new long[]{0, 0};
        res.add(cur);
        for (var instr : instructions) {
            var cur2 = cur;
            var next = switch (instr.dir) {
                case "U" ->
                        IntStream.rangeClosed(1, instr.steps).boxed().map(i -> new long[]{cur2[0] - i, cur2[1]}).toList();
                case "D" ->
                        IntStream.rangeClosed(1, instr.steps).boxed().map(i -> new long[]{cur2[0] + i, cur2[1]}).toList();
                case "L" ->
                        IntStream.rangeClosed(1, instr.steps).boxed().map(i -> new long[]{cur2[0], cur2[1] - i}).toList();
                case "R" ->
                        IntStream.rangeClosed(1, instr.steps).boxed().map(i -> new long[]{cur2[0], cur2[1] + i}).toList();
                default -> throw new Error("unknown direction");
            };
            res.addAll(next);
            cur = next.get(next.size() - 1);
        }

        return res;
    }

    static long[] borderSize(List<long[]> border) {
        return new long[]{
                border.stream().map(a -> a[0]).min(Long::compareTo).get(),
                border.stream().map(a -> a[0]).max(Long::compareTo).get(),
                border.stream().map(a -> a[1]).min(Long::compareTo).get(),
                border.stream().map(a -> a[1]).max(Long::compareTo).get()
        };
    }

    static void fillExternalSpace(long[][] grid) {
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

    static int internalSpace(long[][] grid) {
        int acc = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != 2) acc++;
            }
        }

        return acc;
    }

    record Instruction(String dir, int steps) {
    }
}
