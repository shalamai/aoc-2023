package day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day13/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day13/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        return input(path).stream()
                .map(Solution::solve)
                .reduce(Integer::sum)
                .get();
    }

    static int part2(String path) throws IOException {
        return input(path).stream()
                .map(Solution::solve2)
                .reduce(Integer::sum)
                .get();
    }

    static List<char[][]> input(String path) throws IOException {
        return Arrays.stream(Files.readString(Path.of(path)).split("\n\n"))
                .map(p -> Arrays.stream(p.split("\n"))
                        .map(String::toCharArray)
                        .toArray(char[][]::new))
                .collect(Collectors.toList());

    }

    static int solve(char[][] pattern) {
        for (int i = 0; i < pattern.length - 1; i++)
            if (isMirrorRow(pattern, i)) return 100 * (i + 1);

        for (int i = 0; i < pattern[0].length - 1; i++)
            if (isMirrorCol(pattern, i)) return i + 1;
        return 0;
    }

    static int solve2(char[][] pattern) {
        for (int i = 0; i < pattern.length - 1; i++)
            if (rowDiff(pattern, i) == 1) return 100 * (i + 1);

        for (int i = 0; i < pattern[0].length - 1; i++)
            if (colDiff(pattern, i) == 1) return i + 1;
        return 0;
    }

    static boolean isMirrorRow(char[][] pattern, int row) {
        var r1 = row;
        var r2 = row + 1;

        while (r1 >= 0 && r2 < pattern.length) {
            for (int i = 0; i < pattern[0].length; i++) {
                if (pattern[r1][i] != pattern[r2][i]) return false;
            }
            r1--;
            r2++;
        }

        return true;
    }

    static boolean isMirrorCol(char[][] pattern, int col) {
        var c1 = col;
        var c2 = col + 1;

        while (c1 >= 0 && c2 < pattern[0].length) {
            for (int i = 0; i < pattern.length; i++) {
                if (pattern[i][c1] != pattern[i][c2]) return false;
            }
            c1--;
            c2++;
        }

        return true;
    }

    static int rowDiff(char[][] pattern, int row) {
        var r1 = row;
        var r2 = row + 1;

        int count = 0;
        while (r1 >= 0 && r2 < pattern.length) {
            for (int i = 0; i < pattern[0].length; i++) {
                if (pattern[r1][i] != pattern[r2][i]) count++;
            }
            r1--;
            r2++;
        }

        return count;
    }

    static int colDiff(char[][] pattern, int col) {
        var c1 = col;
        var c2 = col + 1;

        int count = 0;
        while (c1 >= 0 && c2 < pattern[0].length) {
            for (int i = 0; i < pattern.length; i++) {
                if (pattern[i][c1] != pattern[i][c2]) count++;
            }
            c1--;
            c2++;
        }

        return count;
    }
}
