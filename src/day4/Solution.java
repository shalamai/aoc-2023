package day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        int res1 = part1();
        System.out.println(res1);

        int res2 = part2();
        System.out.println(res2);
    }

    static int part1() throws IOException {
        var cards = parseInput("./src/day4/input.txt");
        int acc = 0;
        for (List<List<Integer>> card : cards) {
            Set<Integer> winning = new HashSet<>(card.get(0));
            long n = card.get(1).stream().filter(winning::contains).count();
            if (n > 0) acc += Math.pow(2, n - 1);
        }

        return acc;
    }

    static int part2() throws IOException {
        var cards = parseInput("./src/day4/input.txt");
        var map = matches(cards);

        var countMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < map.size(); i++) {
            var count = countMap.getOrDefault(i, 0) + 1;
            countMap.put(i, count);
            var matches = map.get(i);
            var j = i + 1;
            while(j <= i + matches) {
                countMap.put(j, countMap.getOrDefault(j, 0) + count);
                j++;
            }
        }

        return countMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    static List<List<List<Integer>>> parseInput(String path) throws IOException {
        var lines = Files.readAllLines(Path.of(path));

        List<List<List<Integer>>> res = new ArrayList<>();
        for (String line : lines) {
            var parts = line.split(":")[1].split("\\|");
            var winning = parts[0].split(" ");
            var mine = parts[1].split(" ");

            List<List<Integer>> card = new ArrayList<>();
            card.add(parseNumbers(winning));
            card.add(parseNumbers(mine));
            res.add(card);
        }

        return res;
    }

    static List<Integer> parseNumbers(String[] arr) {
        return Arrays
                .stream(arr)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    static Map<Integer, Integer> matches(List<List<List<Integer>>> cards) {
        Map<Integer, Integer> m = new HashMap<>();

        for (int i = 0; i < cards.size(); i++) {
            Set<Integer> winning = new HashSet<>(cards.get(i).get(0));
            long n = cards.get(i).get(1).stream().filter(winning::contains).count();
            m.put(i, (int) n);
        }

        return m;
    }
}
