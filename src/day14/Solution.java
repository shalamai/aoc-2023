package day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Solution {

    public static void main(String[] args) throws IOException {
//        var res1 = part1("./src/day14/input0.txt");
//        System.out.println(res1);

        var res2 = part2("./src/day14/input.txt");
        System.out.println(res2);
        System.out.println((1000_000_000 - 143) % 39);
    }

    static int part1(String path) throws IOException {
        var map = input(path);
        tiltNorth(map);
        return load(map);
    }

    static int part2(String path) throws IOException {
        var map = input(path);
        for (int i = 0; i < 300; i++) {
            doCycle(map);
            System.out.println(i + ": " + load(map));
        }

        return 0;
    }

    static char[][] input(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    static void tiltNorth(char[][] map) {
        for (int j = 0; j < map[0].length; j++) {
            var last = -1;
            for (int i = 0; i < map.length; i++) {
                if (map[i][j] == '#') last = i;
                if (map[i][j] == 'O') {
                    map[i][j] = '.';
                    map[++last][j] = 'O';
                }
            }
        }
    }

    static void tiltSouth(char[][] map) {
        for (int j = 0; j < map[0].length; j++) {
            var last = map.length;
            for (int i = map.length - 1; i >= 0; i--) {
                if (map[i][j] == '#') last = i;
                if (map[i][j] == 'O') {
                    map[i][j] = '.';
                    map[--last][j] = 'O';
                }
            }
        }
    }

    static void tiltWest(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            var last = -1;
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '#') last = j;
                if (map[i][j] == 'O') {
                    map[i][j] = '.';
                    map[i][++last] = 'O';
                }
            }
        }
    }

    static void tiltEast(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            var last = map[0].length;
            for (int j = map[0].length - 1; j >= 0; j--) {
                if (map[i][j] == '#') last = j;
                if (map[i][j] == 'O') {
                    map[i][j] = '.';
                    map[i][--last] = 'O';
                }
            }
        }
    }

    static void doCycle(char[][] map) {
        tiltNorth(map);
        tiltWest(map);
        tiltSouth(map);
        tiltEast(map);
    }

    static int load(char[][] map) {
        int acc = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'O') acc += map.length - i;
            }
        }
        return acc;
    }
}
