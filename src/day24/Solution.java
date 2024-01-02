package day24;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day24/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day24/input0.txt");
        System.out.println(res2);

    }

    static int part1(String path) throws IOException {
        var hailstones = input(path);

        int acc = 0;
        for (int i = 0; i < hailstones.size(); i++) {
            for (int j = i + 1; j < hailstones.size(); j++) {
                if (hasValidIntersection(hailstones.get(i), hailstones.get(j), new BigDecimal(200000000000000L), new BigDecimal(400000000000000L))) {
                    acc++;
                }
            }
        }

        return acc;
    }

    static int part2(String path) throws IOException {
        var hailstones = input(path);

        var res = new ArrayList<List<Coord>>();

        var h1 = hailstones.get(0);
        var h2 = hailstones.get(1);

        var dot1 = h1.c;
        for (int i = 0; i < 100; i++) {
            var dot2 = h2.c;
            for (int j = 0; j < 100; j++) {
                var rockLine = line3d(dot1, dot2);

                var valid = hailstones.stream().allMatch(h3 -> {
                    if (h3.equals(h1) || h3.equals(h2)) return true;
                    var hailstoneLine = line3d(h3);
                    var intersection = rockLine.intersection(hailstoneLine);
                    return intersection.isPresent() && h3.isValidState(intersection.get());
                });

                if (valid) res.add(List.of(dot1, dot2));

                dot2 = dot2.next(h2.v);
            }
            dot1 = dot1.next(h1.v);
        }

        System.out.println(res);

        return 0;
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

    static BigDecimal num(String a) {
        return new BigDecimal(a.trim());
    }

    static boolean hasValidIntersection(Hailstone h1, Hailstone h2, BigDecimal min, BigDecimal max) {
        var line1 = lineXY(h1);
        var line2 = lineXY(h2);
        var intersectionMaybe = line1.intersection(line2);
        if (intersectionMaybe.isEmpty()) return false;
        var intersection = intersectionMaybe.get();
        if (!h1.isFuture(intersection) || !h2.isFuture(intersection)) return false; // cross in the past
        return intersection.x.compareTo(min) >= 0 && intersection.x.compareTo(max) <= 0 &&
                intersection.y.compareTo(min) >= 0 && intersection.y.compareTo(max) <= 0;
    }

    static Line lineXY(Hailstone h) {
        Coord c2 = h.c.next(h.v);
        return lineXY(h.c, c2);
    }

    static Line lineXY(Coord c1, Coord c2) {
        if (c2.x.subtract(c1.x).compareTo(BigDecimal.ZERO) == 0) return new Line(BigDecimal.ZERO, c1.y);
        var k = c2.y.subtract(c1.y).divide(c2.x.subtract(c1.x), 20, RoundingMode.FLOOR);
        var b = k.negate().multiply(c1.x).add(c1.y);
        return new Line(k, b);
    }

    static Line lineXZ(Hailstone h) {
        Coord c2 = h.c.next(h.v);
        return lineXZ(h.c, c2);
    }

    static Line lineXZ(Coord c1, Coord c2) {
        if (c2.x.subtract(c1.x).compareTo(BigDecimal.ZERO) == 0) return new Line(BigDecimal.ZERO, c1.z);
        var k = c2.z.subtract(c1.z).divide(c2.x.subtract(c1.x), 20, RoundingMode.FLOOR);
        var b = k.negate().multiply(c1.x).add(c1.z);
        return new Line(k, b);
    }

    static Line3D line3d(Hailstone h) {
        return new Line3D(lineXY(h), lineXZ(h));
    }

    static Line3D line3d(Coord c1, Coord c2) {
        return new Line3D(lineXY(c1, c2), lineXZ(c1, c2));
    }

    record Line3D(Line xy, Line xz) {
        Optional<Coord> intersection(Line3D line2) {
            var yInterMaybe = xy.intersection(line2.xy);
            var zInterMaybe = xz.intersection(line2.xz);

            if (yInterMaybe.isEmpty() || zInterMaybe.isEmpty()) return Optional.empty();
            var yInter = yInterMaybe.get();
            var zInter = zInterMaybe.get();

            if (yInter.x.equals(zInter.x))
                return Optional.of(new Coord(yInter.x, yInter.y, zInter.y)); // yInter.y actually means z here
            else return Optional.empty();
        }
    }

    record Line(BigDecimal k, BigDecimal b) {
        Optional<CoordXY> intersection(Line line2) {
            if (k.equals(line2.k)) return Optional.empty(); // parallel
            var x = line2.b.subtract(b).divide(k.subtract(line2.k), 0, RoundingMode.HALF_UP);
            var y = k.multiply(x).add(b).setScale(0, RoundingMode.HALF_UP);
            return Optional.of(new CoordXY(x, y));
        }
    }

    record Hailstone(Coord c, Velocity v) {
        boolean isFuture(CoordXY c2) {
            return (v.x.multiply(c2.x.subtract(c.x)).compareTo(BigDecimal.ZERO) > 0) ||
                    (v.y.multiply(c2.y.subtract(c.y)).compareTo(BigDecimal.ZERO) > 0);
        }

        boolean isValidState(Coord c2) {
            var validX = (c2.x.subtract(c.x)).remainder(v.x).equals(BigDecimal.ZERO) &&
                    c2.x.subtract(c.x).multiply(v.x).compareTo(BigDecimal.ZERO) > 0;
            var validY = (c2.y.subtract(c.y)).remainder(v.y).equals(BigDecimal.ZERO) &&
                    c2.y.subtract(c.y).multiply(v.y).compareTo(BigDecimal.ZERO) > 0;
            var validZ = (c2.z.subtract(c.z)).remainder(v.z).equals(BigDecimal.ZERO) &&
                    c2.z.subtract(c.z).multiply(v.z).compareTo(BigDecimal.ZERO) > 0;

            return validX && validY && validZ;
        }
    }

    record CoordXY(BigDecimal x, BigDecimal y) {
    }

    record Coord(BigDecimal x, BigDecimal y, BigDecimal z) {
        Coord next(Velocity v) {
            return new Coord(x.add(v.x), y.add(v.y), z.add(v.z));
        }
    }

    record Velocity(BigDecimal x, BigDecimal y, BigDecimal z) {
    }
}
