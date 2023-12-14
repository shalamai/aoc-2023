package day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// todo
// - try with strings and copies of arrays
public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day12/input.txt");
        System.out.println(res1);

//        System.out.println(solve("????.######..#####.", List.of(1,6,5), ""));
//        System.out.println(".?##adf".matches("^(#|\\?){" + 3 + "}.*"));
    }

    static int part1(String path) throws IOException {
        var as = records1(path);
        var bs = records2(path);

        int acc = 0;
        for (int i = 0; i < as.size(); i++) {
            acc += solve(as.get(i), bs.get(i), "");
        }

        return acc;
    }

    static List<String> records1(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
//                .map(l -> l.split(" ")[0].chars().mapToObj(c -> (char) c).collect(Collectors.toList()))
                .map(l -> l.split(" ")[0])
                .collect(Collectors.toList());
    }

    static List<List<Integer>> records2(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream()
                .map(l -> Arrays.stream(l.split(" ")[1].split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    static int solve(String record1, List<Integer> record2, String res) {
//        System.out.println(record1 + " / " + record2);
        if (record1.isEmpty()) {
            if (record2.isEmpty() || record2.equals(List.of(0))) {
//                System.out.println(res);
                return 1;
            } else return 0;
        }
        if (record2.isEmpty()) {
            if (record1.contains("#")) return 0;
            else {
//                System.out.println(res);
                return 1;
            }
        }
        if (record2.get(0) == 0)
            return record1.charAt(0) == '#' ? 0 : solve(record1.substring(1), record2.subList(1, record2.size()), res + ".");

        if (record1.charAt(0) == '.') {
            return solve(record1.substring(1), record2, res + ".");
        }
        if (record1.charAt(0) == '#') {
            if (record1.matches("^(#|\\?){" + record2.get(0) + "}.*")) {
                return solve(record1.substring(record2.get(0)), decrHead(record2), res + "#".repeat(record2.get(0)));
            } else return 0;
//            return solve(record1.substring(1), decrHead(record2), res + "#");
        }

        // '?' case
        var acc = solve(record1.substring(1), record2, res + ".");
        if (record1.matches("^(#|\\?){" + record2.get(0) + "}.*")) {
            acc += solve(record1.substring(record2.get(0)), decrHead(record2), res + "#".repeat(record2.get(0)));
        }
//        var r2 = solve(record1.substring(1), decrHead(record2), res + "#");
        return acc;
    }

    static List<Integer> decrHead(List<Integer> as) {
        var as2 = new ArrayList<>(as);
//        as2.set(0, as2.get(0) - 1);
        as2.set(0, 0);
//        if (as2.get(0) == 0) as2.remove(0);
        return as2;
    }
}
