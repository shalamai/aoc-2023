package day22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day22/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day22/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        var bricks = input(path);
        bricks = land(bricks);
        linkUnder(bricks);

        int acc = 0;
        for (var brick : bricks)
            if (canDisintegrate(bricks, brick)) acc++;

        return acc;
    }

    static int part2(String path) throws IOException {
        var bricks = input(path);
        bricks = land(bricks);
        linkUnder(bricks);
        linkTopFromUnder(bricks);

        var acc = 0;
        for (var brick : bricks)
            acc += criticalFor(brick);

        return acc;
    }

    static List<Brick> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(l -> {
                    var parts = l.split("~");
                    return new Brick(parseCoord(parts[0]), parseCoord(parts[1]), new ArrayList<>(), new ArrayList<>());
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

    static void linkUnder(List<Brick> bricks) {
        for (Brick brick : bricks) {
            if (brick.from.z == 1) continue;
            var under = bricks.stream()
                    .filter(b -> b.to.z == brick.from.z - 1)
                    .filter(b -> isXYIntersection(b, brick.shiftDown()))
                    .toList();

            brick.under.addAll(under);
        }
    }

    static void linkTopFromUnder(List<Brick> bricks) {
        for (var brick : bricks) {
            brick.top.addAll(bricks.stream().filter(b -> b.under.contains(brick)).toList());
        }
    }

    static int criticalFor(Brick brick0) {
        Queue<Brick> q = new LinkedList<>();
        q.add(brick0);
        var removed = new HashSet<Brick>();
        removed.add(brick0);

        while (!q.isEmpty()) {
            var brick = q.poll();
            var toppled = brick.top
                    .stream()
                    .filter(b -> removed.containsAll(b.under))
                    .toList();

            for (var tb : toppled)
                if (!removed.contains(tb))
                    q.add(tb);

            removed.addAll(toppled);
        }

        removed.remove(brick0);

        return removed.size();
    }

    static boolean canDisintegrate(List<Brick> bricks, Brick brick) {
        return bricks.stream().filter(b -> b.under.contains(brick)).noneMatch(b -> b.under.size() == 1);
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

        @Override
        public String toString() {
            return "{" + x + ", " + y + ", " + z + "}";
        }
    }

    record Brick(Coord from, Coord to, List<Brick> under, List<Brick> top) {

        boolean canShiftDown() {
            return from.z > 1;
        }

        Brick shiftDown() {
            return new Brick(from.shiftDown(), to.shiftDown(), under, top);
        }

        @Override
        public String toString() {
            return "(" + from + "~" + to + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Brick brick = (Brick) o;
            return Objects.equals(from, brick.from) && Objects.equals(to, brick.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }
}