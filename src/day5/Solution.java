package day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {
    public static void main(String[] args) throws IOException {
        System.out.println(part1("./src/day5/input.txt"));
    }

    static long part1(String path) throws IOException {
        var maps = maps(path);

        return seeds(path)
                .stream()
                .map(s -> seed2location(s, maps))
                .min(Long::compare)
                .get();
    }

    static List<Long> seeds(String path) throws IOException {
        return Arrays.stream(Files.readAllLines(Path.of(path))
                        .get(0)
                        .split(": ")[1]
                        .split(" "))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    static List<List<List<Long>>> maps(String path) throws IOException {
        String[] parts = Files.readString(Path.of(path)).split("\n\n.*map:\n");
        var res = new ArrayList<List<List<Long>>>();

        for (int i = 1; i < parts.length; i++) {
            var ranges = Arrays.stream(parts[i].split("\n"))
                    .map(r -> Arrays.stream(r.split(" "))
                            .map(Long::parseLong)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());

            res.add(ranges);
        }

        return res;
    }

    static long seed2location(long src, List<List<List<Long>>> maps) {
        for (var map : maps) {
            long src2 = src;
            var mapping = map
                    .stream()
                    .filter(m -> src2 >= m.get(1) && src2 <= m.get(1) + m.get(2))
                    .findFirst();
            if (mapping.isPresent()) src = src + mapping.get().get(0) - mapping.get().get(1);
        }

        return src;
    }
}
