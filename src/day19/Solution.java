package day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {


    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day19/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var parts = parseParts(path);
        var workflows = parseWorkflows(path);

        int acc = 0;
        for (var part : parts)
            if (isAccepted(part, workflows)) acc += part.total();

        return acc;
    }

    static List<Part> parseParts(String path) throws IOException {
        return Files.readString(Path.of(path)).split("\n\n")[1]
                .lines()
                .map(l -> {
                    var parts = l.split(",");
                    return new Part(Map.of(
                            Rating.x, Integer.parseInt(parts[0].split("=")[1]),
                            Rating.m, Integer.parseInt(parts[1].split("=")[1]),
                            Rating.a, Integer.parseInt(parts[2].split("=")[1]),
                            Rating.s, Integer.parseInt(withoutLastChar(parts[3].split("=")[1]))
                    ));
                })
                .toList();
    }

    static Map<String, Workflow> parseWorkflows(String path) throws IOException {
        var map = new HashMap<String, Workflow>();

        var lines = Files.readString(Path.of(path)).split("\n\n")[0].split("\n");
        for (var line : lines) {
            var parts = line.split("\\{");
            var name = parts[0];
            var parts2 = parts[1].split(",");
            var rules = new ArrayList<Rule>();
            for (int i = 0; i < parts2.length - 1; i++) {
                var ruleLineParts = parts2[i].split(":");
                var rating = switch (ruleLineParts[0].charAt(0)) {
                    case 'x' -> Rating.x;
                    case 'm' -> Rating.m;
                    case 'a' -> Rating.a;
                    case 's' -> Rating.s;
                    default -> throw new Error("invalid rating");
                };
                var op = switch (ruleLineParts[0].charAt(1)) {
                    case '>' -> Operator.more;
                    case '<' -> Operator.less;
                    default -> throw new Error("invalid operator");
                };
                var val = Integer.parseInt(ruleLineParts[0].substring(2));
                rules.add(new Rule(rating, op, val, ruleLineParts[1]));
            }

            map.put(name, new Workflow(rules, withoutLastChar(parts2[parts2.length - 1])));
        }

        return map;
    }

    static boolean isAccepted(Part part, Map<String, Workflow> workflows) {
        var workflow = "in";

        while (!workflow.equals("A") && !workflow.equals("R")) {
            workflow = workflows.get(workflow).rules
                    .stream()
                    .filter(rule ->
                            rule.op == Operator.more && part.ratings.get(rule.rating) > rule.val ||
                                    rule.op == Operator.less && part.ratings.get(rule.rating) < rule.val)
                    .map(Rule::dst)
                    .findFirst()
                    .orElse(workflows.get(workflow).fallback);
        }

        return Objects.equals(workflow, "A");
    }

    static String withoutLastChar(String a) {
        return a.substring(0, a.length() - 1);
    }

    enum Rating {
        x, m, a, s
    }

    enum Operator {
        more, less
    }

    record Part(Map<Rating, Integer> ratings) {
        int total() {
            return ratings.values().stream().reduce(Integer::sum).get();
        }
    }


    record Workflow(List<Rule> rules, String fallback) {
    }

    record Rule(Rating rating, Operator op, int val, String dst) {
    }
}
