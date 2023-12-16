package day16;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day16/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var grid = input(path);

        var beams = new HashSet<Beam>();
        Queue<Beam> q = new LinkedList<>();
        q.add(new Beam(0, 0, '>'));

        char[][] debug = new char[grid.length][grid[0].length];
        for (char[] row : debug) Arrays.fill(row, '.');

        while (!q.isEmpty()) {
            var b = q.poll();
            if (beams.contains(b)) continue;
            beams.add(b);
            debug[b.i()][b.j()] = '#';
            q.addAll(b.next(grid));
        }

        Arrays.stream(debug).forEach(System.out::println);

        return beams.stream().map(b -> b.i() + ":" + b.j()).collect(Collectors.toSet()).size();
    }

    static char[][] input(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }
}


record Beam(int i, int j, char dir) {
    List<Beam> next(char[][] grid) {
        return apply(action(grid)).stream()
                .filter(b -> b.i >= 0 && b.i < grid.length && b.j >= 0 && b.j < grid[0].length)
                .toList();
    }

    private Action action(char[][] grid) {
        return switch (String.valueOf(dir) + grid[i][j]) {
            case "<.", "<-", "i\\", "j/" -> Action.LEFT;
            case ">.", ">-", "i/", "j\\" -> Action.RIGHT;
            case "i.", "i|", ">/", "<\\" -> Action.UP;
            case "j.", "j|", ">\\", "</" -> Action.DOWN;
            case ">|", "<|" -> Action.SPLIT_VER;
            case "i-", "j-" -> Action.SPLIT_HOR;
            default -> throw new Error("invalid beam");

        };
    }

    private List<Beam> apply(Action a) {
        return switch (a) {
            case UP -> List.of(new Beam(i - 1, j, 'i'));
            case DOWN -> List.of(new Beam(i + 1, j, 'j'));
            case LEFT -> List.of(new Beam(i, j - 1, '<'));
            case RIGHT -> List.of(new Beam(i, j + 1, '>'));
            case SPLIT_HOR -> List.of(new Beam(i, j - 1, '<'), new Beam(i, j + 1, '>'));
            case SPLIT_VER -> List.of(new Beam(i - 1, j, 'i'), new Beam(i + 1, j, 'j'));
        };
    }

    private enum Action {
        UP, DOWN, LEFT, RIGHT, SPLIT_VER, SPLIT_HOR
    }
}