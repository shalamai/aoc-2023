package day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day15/input.txt");
        System.out.println(res1);

        var res2 = part2("./src/day15/input.txt");
        System.out.println(res2);
    }

    static int part1(String path) throws IOException {
        return Arrays.stream(input(path))
                .map(Solution::hash)
                .reduce(Integer::sum)
                .get();
    }

    static int part2(String path) throws IOException {
        var boxes = fold(input2(path)).stream().collect(Collectors.groupingBy(i -> hash(i.label)));
        var acc = 0;

        for (var entry : boxes.entrySet()) {
            var lenses = entry.getValue();
            for (int i = 0; i < lenses.size(); i++) {
                acc += (entry.getKey() + 1) * (i + 1) * lenses.get(i).focalLength;
            }
        }

        return acc;
    }

    static String[] input(String path) throws IOException {
        return Files.readString(Path.of(path)).split(",");
    }

    static List<Instruction> input2(String path) throws IOException {
        return Arrays.stream(Files.readString(Path.of(path)).split(","))
                .map(a -> a.contains("-") ? new Rm(a) : new Add(a))
                .collect(Collectors.toList());
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

    static List<Add> fold(List<Instruction> as) {
        Map<String, Add> res = new LinkedHashMap<>();

        for (int i = 0; i < as.size(); i++) {
            var inst = as.get(i);
            if (inst instanceof Add) res.put(inst.label, (Add) inst);
            else res.remove(inst.label);
        }

        return new ArrayList<>(res.values());
    }
}

class Instruction {
    String label;

    Instruction(String label) {
        this.label = label;
    }
}

class Rm extends Instruction {

    Rm(String input) {
        super(input.substring(0, input.length() - 1));
    }
}

class Add extends Instruction {
    int focalLength;

    Add(String input) {
        super(input.split("=")[0]);
        this.focalLength = Integer.parseInt(input.split("=")[1]);
    }

    @Override
    public String toString() {
        return "Add{" +
                "focalLength=" + focalLength +
                ", label='" + label + '\'' +
                '}';
    }
}
