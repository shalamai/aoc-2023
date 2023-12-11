package day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day6/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day6/input.txt");
        System.out.println(res2);
    }

    static long part1(String path) throws IOException {
        var races = input(path);
        long acc = 1;

        for (var race : races) {
            acc *= ways(race);
        }

        return acc;
    }

    static long part2(String path) throws IOException {
        var races = input2(path);
        long acc = 1;

        for (var race : races) {
            acc *= ways(race);
        }

        return acc;
    }

    static List<Race> input(String path) throws IOException {
        var lines = Files.readAllLines(Path.of(path));
        var times = Arrays.stream(lines.get(0).split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        var distances = Arrays.stream(lines.get(1).split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Race> res = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            res.add(new Race(times.get(i), distances.get(i)));
        }

        return res;
    }

    static List<Race> input2(String path) throws IOException {
        var lines = Files.readAllLines(Path.of(path));
        var time = Long.parseLong(lines.get(0).split(":")[1].replaceAll(" ", ""));
        var distance = Long.parseLong(lines.get(1).split(":")[1].replaceAll(" ", ""));
        List<Race> res = new ArrayList<>();
        res.add(new Race(time, distance));
        return res;
    }

    static long ways(Race race) {
        var m = min(race);
        return race.time - 2 * m + 1;
    }

    static long min(Race race) {
        var l = 1L;
        var r = race.time / 2;
        long mid;

        while (l < r) {
            mid = (l + r) / 2;
            var dist = mid * (race.time - mid);
            if (dist <= race.distance) l = mid + 1;
            else r = mid;
        }

        return r;
    }
}

class Race {
    long time;
    long distance;

    Race(long t, long d) {
        this.time = t;
        this.distance = d;
    }

    @Override
    public String toString() {
        return "Race{" +
                "time=" + time +
                ", distance=" + distance +
                '}';
    }
}
