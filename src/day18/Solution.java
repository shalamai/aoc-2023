package day18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day18/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day18/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        var instructions = input(path);
        var border = border(instructions);
        var size = borderSize(border);
        var gridI = size[1] - size[0] + 1 + 2;
        var gridJ = size[3] - size[2] + 1 + 2;
        var grid = new char[(int) gridI][(int) gridJ];

        for (long[] coord : border) grid[(int) (coord[0] - size[0] + 1)][(int) (coord[1] - size[2] + 1)] = '#';
        fillExternalSpace(grid);

        return internalSpace(grid);
    }

    static long part2(String path) throws IOException {
        var instructions = input2(path);
        var ranges = border2(instructions);

        long acc = 0;

        while (!ranges.isEmpty()) {
            var curRow = ranges.stream().min(Comparator.comparing(Range::row)).get().row;
            var curRange = ranges.stream().filter(r -> r.row == curRow).findFirst().get();
            var underRanges = curRange.underRanges(ranges);
            var underRow = underRanges.get(0).row;

            var underRangesSplit = underRanges.stream().collect(Collectors.groupingBy(b -> b.from < curRange.from || b.to > curRange.to));
            var in = underRangesSplit.getOrDefault(false, List.of());
            var out = underRangesSplit.getOrDefault(true, List.of());
            var merged = curRange.merge(out);
            var diff = merged.diff(in).stream().map(r -> r.withRow(underRow)).toList();

            acc += curRange.length() * (underRow - curRange.row);
            acc += merged.length() - diff.stream().map(Range::length).reduce(Long::sum).orElse(0L);

            ranges.addAll(diff);
            ranges.remove(curRange);
            underRanges.forEach(ranges::remove);
        }

        return acc;
    }

    static List<Instruction> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().map(line -> {
            var parts = line.split(" ");
            return new Instruction(parts[0], Integer.parseInt(parts[1]));
        }).toList();
    }

    static List<Instruction> input2(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().map(line -> {
            var hex = line.split(" ")[2];
            var dir = switch (hex.substring(7, 8)) {
                case "0" -> "R";
                case "1" -> "D";
                case "2" -> "L";
                case "3" -> "U";
                default -> throw new Error("invalid direction code");
            };
            return new Instruction(dir, Integer.parseInt(hex.substring(2, 7), 16));
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

    static Set<Range> border2(List<Instruction> instructions) {
        var ranges = new HashSet<Range>();

        var cur = new long[]{0, 0};
        for (var instr : instructions) {
            switch (instr.dir) {
                case "U":
                    cur[0] -= instr.steps;
                    break;
                case "D":
                    cur[0] += instr.steps;
                    break;
                case "L":
                    ranges.add(new Range(cur[1] - instr.steps, cur[1], cur[0]));
                    cur[1] -= instr.steps;
                    break;
                case "R":
                    ranges.add(new Range(cur[1], cur[1] + instr.steps, cur[0]));
                    cur[1] += instr.steps;
                    break;
            }
        }

        return ranges;
    }

    static long[] borderSize(List<long[]> border) {
        return new long[]{
                border.stream().map(a -> a[0]).min(Long::compareTo).get(),
                border.stream().map(a -> a[0]).max(Long::compareTo).get(),
                border.stream().map(a -> a[1]).min(Long::compareTo).get(),
                border.stream().map(a -> a[1]).max(Long::compareTo).get()
        };
    }

    static void fillExternalSpace(char[][] grid) {
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{0, 0});

        while (!q.isEmpty()) {
            var coord = q.poll();
            var i = coord[0];
            var j = coord[1];

            if (grid[i][j] == '.' || grid[i][j] == '#') continue;

            grid[i][j] = '.';
            if (i > 0) q.add(new int[]{i - 1, j});
            if (i < grid.length - 1) q.add(new int[]{i + 1, j});
            if (j > 0) q.add(new int[]{i, j - 1});
            if (j < grid[0].length - 1) q.add(new int[]{i, j + 1});
        }
    }

    static int internalSpace(char[][] grid) {
        int acc = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != '.') acc++;
            }
        }

        return acc;
    }

    record Instruction(String dir, int steps) {
    }

    record Range(long from, long to, long row) {

        List<Range> underRanges(Set<Range> ranges) {
            var rangesBelow = ranges.stream()
                    .filter(r -> r.row > this.row)
                    .filter(r -> (r.from >= this.from && r.from <= this.to) ||
                            (r.to >= this.from && r.to <= this.to))
                    .toList();

            var closestRow = rangesBelow.stream().min(Comparator.comparing(Range::row)).get().row;

            return rangesBelow.stream().filter(r -> r.row == closestRow).toList();
        }

        Range merge(List<Range> ranges) {
            if (ranges.isEmpty()) return this;

            var from = ranges.stream().map(Range::from).min(Long::compareTo).get();
            var to = ranges.stream().map(Range::to).max(Long::compareTo).get();

            return new Range(Math.min(this.from, from), Math.max(this.to, to), this.row);
        }

        Range withRow(long row) {
            return new Range(this.from, this.to, row);
        }

        List<Range> diff(List<Range> ranges) {
            if (ranges.isEmpty()) return List.of(this);

            var res = new ArrayList<Range>();
            ranges.sort(Comparator.comparing(r -> r.from));

            var from = this.from;
            for (var r : ranges) {
                res.add(new Range(from, r.from, this.row));
                from = r.to;
            }
            res.add(new Range(from, this.to, this.row));

            return res.stream().filter(r -> r.from != r.to).toList();
        }

        long length() {
            return this.to - this.from + 1;
        }
    }
}
