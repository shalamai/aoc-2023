package day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {

    public static void main(String[] args) throws IOException {
        int res1 = part1();
        System.out.println(res1);

        int res2 = part2();
        System.out.println(res2);
    }

    static int part1() throws IOException {
        var m = matrix("./src/day3/input.txt");
        var acc = 0;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (!Character.isDigit(m[i][j]) && m[i][j] != '.') {
                    for (int[] d : digitsAround(m, i, j)) {
                        var n = parseNumberAndErase(m, d[0], d[1]);
                        if (n != -1) acc += n;
                    }
                }
            }
        }

        return acc;
    }

    static int part2() throws IOException {
        var m = matrix("./src/day3/input.txt");
        var acc = 0;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] == '*') {
                    var digits = digitsAround(m, i, j);
                    var m2 = cp(m);
                    List<Integer> numbers = new ArrayList<>();
                    for (int[] d : digits) {
                        int n = parseNumberAndErase(m2, d[0], d[1]);
                        if (n != -1) numbers.add(n);
                    }
                    if (numbers.size() == 2) {
                        acc += numbers.get(0) * numbers.get(1);
                    }
                }
            }
        }

        return acc;
    }

    static char[][] matrix(String path) throws IOException {
        var lines = Files.readAllLines(Path.of(path));
        var m = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < m.length; i++) {
            m[i] = lines.get(i).toCharArray();
        }

        return m;
    }

    static char[][] cp(char[][] m) {
        var res = new char[m.length][m[0].length];
        for (int i = 0; i < m.length; i++) {
            res[i] = Arrays.copyOf(m[i], m.length);
        }
        return res;
    }

    static List<int[]> digitsAround(char[][] matrix, int i, int j) {
        var coords = new int[][]{
                new int[]{i - 1, j},
                new int[]{i + 1, j},
                new int[]{i, j - 1},
                new int[]{i, j + 1},
                new int[]{i - 1, j - 1},
                new int[]{i - 1, j + 1},
                new int[]{i + 1, j - 1},
                new int[]{i + 1, j + 1},
        };

        var res = new ArrayList<int[]>();
        for (int[] c : coords) {
            int i0 = c[0];
            int j0 = c[1];

            if (i0 >= 0 && i0 < matrix.length && j0 >= 0 && j0 < matrix[0].length && Character.isDigit(matrix[i0][j0])) {
                res.add(c);
            }
        }

        return res;
    }

    static int parseNumberAndErase(char[][] matrix, int i, int j) {
        if (matrix[i][j] == '.') return -1;
        var number = new StringBuilder();
        number.append(matrix[i][j]);
        matrix[i][j] = '.';

        var j2 = j - 1;
        while (j2 >= 0 && Character.isDigit(matrix[i][j2])) {
            number.insert(0, matrix[i][j2]);
            matrix[i][j2] = '.';
            j2--;
        }

        j2 = j + 1;
        while (j2 < matrix[0].length && Character.isDigit(matrix[i][j2])) {
            number.append(matrix[i][j2]);
            matrix[i][j2] = '.';
            j2++;
        }

        return Integer.parseInt(number.toString());
    }
}
