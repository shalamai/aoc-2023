package day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution2 {
    public static void main(String[] args) throws IOException {
        System.out.println(part2("./src/day5/input.txt"));
    }

    static Comparator<Range> rangeComparator = (a, b) -> (a.from > b.from ? 1 : -1);
    static Comparator<Mapping> mappingComparator = (a, b) -> rangeComparator.compare(a.range, b.range);

    static long part2(String path) throws IOException {
        var seeds = seeds(path);
        var maps = maps(path);

        List<Range> locations = new ArrayList<>();
        for (Range seedRange : seeds) {
            List<Range> locationRanges = seedRange2locationRanges(seedRange, maps);
            locations.addAll(locationRanges);
        }

        locations.sort(rangeComparator);
        return locations.get(0).from;
    }

    static List<Range> seeds(String path) throws IOException {
        var as = Arrays.stream(Files.readAllLines(Path.of(path))
                        .get(0)
                        .split(": ")[1]
                        .split(" "))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        var res = new ArrayList<Range>();

        for (int i = 0; i < as.size(); i += 2) {
            res.add(new Range(as.get(i), as.get(i) + as.get(i + 1) - 1));
        }

        return res;
    }

    static List<List<Mapping>> maps(String path) throws IOException {
        String[] parts = Files.readString(Path.of(path)).split("\n\n.*map:\n");
        var res = new ArrayList<List<Mapping>>();

        for (int i = 1; i < parts.length; i++) {
            var ranges = Arrays.stream(parts[i].split("\n"))
                    .map(r -> Arrays.stream(r.split(" "))
                            .map(Long::parseLong)
                            .collect(Collectors.toList()))
                    .map(l -> new Mapping(new Range(l.get(1), l.get(1) + l.get(2) - 1), l.get(0) - l.get(1)))
                    .collect(Collectors.toList());

            res.add(ranges);
        }

        return res;
    }

    static List<Range> seedRange2locationRanges(Range src, List<List<Mapping>> maps) {
        List<Range> rs = new ArrayList<>();
        rs.add(src);

        for (List<Mapping> mappings : maps) {
            List<Range> rs2 = new ArrayList<>();
            for (Range r : rs) {
                rs2.addAll(mapRange(r, mappings));
            }
            rs = rs2;
        }

        return rs;
    }

    static List<Range> mapRange(Range r, List<Mapping> mappings) {
        List<Range> res = new ArrayList<>();

        mappings.sort(mappingComparator);

        for (Mapping m : mappings) {
            if (r.from > m.range.to) continue;
            if (r.to < m.range.from) {
                res.add(r);
                return res;
            }

            Range intersection = intersection(m.range, r);
            res.add(new Range(intersection.from + m.diff, intersection.to + m.diff));
            leftDiff(r, m.range).ifPresent(res::add);
            var rightDiff = rightDiff(r, m.range);
            if (rightDiff.isPresent()) {
                r = rightDiff.get();
            } else {
                return res;
            }
        }

        res.add(r);
        return res;
    }

    static Range intersection(Range a, Range b) {
        return new Range(Math.max(a.from, b.from), Math.min(a.to, b.to));
    }

    static Optional<Range> leftDiff(Range a, Range b) {
        if (a.from < b.from) return Optional.of(new Range(a.from, b.from - 1));
        else return Optional.empty();
    }

    static Optional<Range> rightDiff(Range a, Range b) {
        if (a.to > b.to) return Optional.of(new Range(b.to + 1, a.to));
        else return Optional.empty();
    }
}

class Range {
    long from, to;

    Range(long from, long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[" + this.from + " - " + this.to + "]";
    }
}

class Mapping {
    Range range;
    Long diff;

    Mapping(Range range, Long diff) {
        this.range = range;
        this.diff = diff;
    }

    @Override
    public String toString() {
        return this.range.toString() + " / diff: " + this.diff;
    }
}

