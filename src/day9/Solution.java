package day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        int res1 = part1("./src/day9/input.txt");
        System.out.println(res1);

        int res2 = part2("./src/day9/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        return input(path).stream()
                .map(Solution::extrapolateNext)
                .reduce(Integer::sum)
                .get();
    }

    static int part2(String path) throws IOException {
        return input(path).stream()
                .map(Solution::extrapolatePrev)
                .reduce(Integer::sum)
                .get();
    }

    static List<List<Integer>> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(l -> Arrays.stream(l.split(" "))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    static int extrapolateNext(List<Integer> as) {
        int acc = 0;

        while (!as.stream().allMatch(a -> a == 0)) {
            acc += as.get(as.size() - 1);
            var as2 = new ArrayList<Integer>();
            for (int i = 0; i < as.size() - 1; i++) {
                as2.add(as.get(i + 1) - as.get(i));
            }
            as = as2;
        }

        return acc;
    }
    static int extrapolatePrev(List<Integer> as) {
        Stack<Integer> stack = new Stack<>();
        while (!as.stream().allMatch(a -> a == 0)) {
            stack.push(as.get(0));
            var as2 = new ArrayList<Integer>();
            for (int i = 0; i < as.size() - 1; i++) {
                as2.add(as.get(i + 1) - as.get(i));
            }
            as = as2;
        }

        int acc = 0;
        while (!stack.isEmpty()) acc = stack.pop() - acc;

        return acc;
    }
}
