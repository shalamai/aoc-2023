package day24;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day24/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var hailstones = input(path);

        int acc = 0;
        for (int i = 0; i < hailstones.size(); i++) {
            for (int j = i + 1; j < hailstones.size(); j++) {
                if (hasValidIntersection(hailstones.get(i), hailstones.get(j), 200000000000000L, 400000000000000L)) {
                    System.out.println(hailstones.get(i) + " - " + hailstones.get(j));
                    acc++;
                }
            }
        }

        return acc;
    }

    static List<Hailstone> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(line -> {
                    var parts = line.split("@");
                    var coords = parts[0].split(",");
                    var velocity = parts[1].split(",");
                    return new Hailstone(
                            new Coord(num(coords[0]), num(coords[1]), num(coords[2])),
                            new Velocity(num(velocity[0]), num(velocity[1]), num(velocity[2]))
                    );
                }).toList();
    }

    static double num(String a) {
        return Double.parseDouble(a.trim());
    }

    static boolean hasValidIntersection(Hailstone h1, Hailstone h2, long min, long max) {
        var line1 = line(h1);
        var line2 = line(h2);
        if (line1.k == line2.k) return false; // parallel
        var intersection = line1.intersection(line2);
        if (!h1.isFuture(intersection) || !h2.isFuture(intersection)) return false; // cross in the past
        return intersection.x >= min && intersection.x <= max && intersection.y >= min && intersection.y <= max;
    }

    static Line line(Hailstone h) {
        Coord c2 = h.c.next(h.v);
        double k = (c2.y - h.c.y) / (c2.x - h.c.x);
        double b = -k * h.c.x + h.c.y;
        return new Line(k, b);
    }

    record Line(double k, double b) {
        Coord intersection(Line line2) {
            var x = (line2.b - b) / (k - line2.k);

            var y = k * x + b;
            return new Coord(x, y, 0);
        }
    }

    record Hailstone(Coord c, Velocity v) {
        boolean isFuture(Coord c2) {
            return (v.x * (c2.x - c.x) > 0) || (v.y * (c2.y - c.y) > 0);
        }
    }

    record Coord(double x, double y, double z) {
        Coord next(Velocity v) {
            return new Coord(x + v.x, y + v.y, z + v.z);
        }
    }

    record Velocity(double x, double y, double z) {
    }
}
