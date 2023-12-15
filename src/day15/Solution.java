package day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day15/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        return Arrays.stream(input(path))
                .map(Solution::hash)
                .reduce(Integer::sum)
                .get();
    }

    static String[] input(String path) throws IOException {
        return Files.readString(Path.of(path)).split(",");
    }

    static int hash(String a) {
        int acc = 0;

        for (int i = 0; i < a.length(); i++) {
            acc += a.charAt(i);
            acc *= 17;
            acc %= 256;
        }

        return acc;
    }
}
