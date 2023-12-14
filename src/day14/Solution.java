package day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day14/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var map = input(path);
        tiltNorth(map);
        for (int i = 0; i < map.length; i++) {
            System.out.println(Arrays.toString(map[i]));
        }
        return load(map);
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
