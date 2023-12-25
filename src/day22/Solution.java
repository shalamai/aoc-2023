package day22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day22/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var bricks = input(path);
        bricks = land(bricks);
        link(bricks);

        int acc = 0;
        for (var brick : bricks)
            if (canDisintegrate(bricks, brick)) acc++;

        return acc;
    }

    static List<Brick> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(l -> {
                    var parts = l.split("~");
                    return new Brick(parseCoord(parts[0]), parseCoord(parts[1]), new ArrayList<>());
                })
                .toList();
    }

    static Coord parseCoord(String input) {
        var parts = input.split(",");
        return new Coord(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    static List<Brick> land(List<Brick> bricks) {
        bricks = new ArrayList<>(bricks);
        bricks.sort(Comparator.comparing(brick -> brick.from.z));
        var res = new ArrayList<Brick>();

        for (var brick : bricks) {
            while (brick.canShiftDown() && !isXYIntersection(res, brick.shiftDown()))
                brick = brick.shiftDown();

            res.add(brick);
        }

        return res;
    }

    static void link(List<Brick> bricks) {
        for (Brick brick : bricks) {
            if (brick.from.z == 1) continue;
            var under = bricks.stream()
                    .filter(b -> b.to.z == brick.from.z - 1)
                    .filter(b -> isXYIntersection(b, brick.shiftDown()))
                    .toList();

            brick.underBricks.addAll(under);
        }
    }

    static boolean canDisintegrate(List<Brick> bricks, Brick brick) {
        return bricks.stream().filter(b -> b.underBricks.contains(brick)).noneMatch(b -> b.underBricks.size() == 1);
    }

    static boolean isXYIntersection(Brick a, Brick b) {
        return isXYIntersection(List.of(a), b);
    }

    static boolean isXYIntersection(List<Brick> bricks, Brick brick) {
        for (var brick2 : bricks.stream().filter(b -> b.to.z == brick.from.z).toList()) {
            var noXIntersection = brick.from.x > brick2.to.x || brick2.from.x > brick.to.x;
            var noYIntersection = brick.from.y > brick2.to.y || brick2.from.y > brick.to.y;
            var canPlace = noXIntersection || noYIntersection;
            if (!canPlace) return true;
        }

        return false;
    }

    record Coord(int x, int y, int z) {
        Coord shiftDown() {
            return new Coord(x, y, z - 1);
        }
    }

    record Brick(Coord from, Coord to, List<Brick> underBricks) {

        boolean canShiftDown() {
            return from.z > 1;
        }

        Brick shiftDown() {
            return new Brick(from.shiftDown(), to.shiftDown(), underBricks);
        }
    }
}