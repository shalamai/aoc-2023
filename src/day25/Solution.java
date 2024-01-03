package day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {

    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day25/input.txt");
        System.out.println(res1);
    }

    static int part1(String path) throws IOException {
        var g = input(path);

        var a0 = g.keySet().stream().toList().get(0);

        var teamA = new HashSet<String>();
        var teamB = new HashSet<String>();
        teamA.add(a0);

        Queue<String> q = new LinkedList<>();
        q.add(a0);

        while (!q.isEmpty()) {
            var a = q.poll();

            var neighbours = g.get(a).stream().filter(a2 -> !teamA.contains(a2) && !teamB.contains(a2)).toList();
            for (var n : neighbours) {
                var ps = paths(g, a, n);

                if (ps > 3) {
                    teamA.add(n);
                    q.add(n);
                }
                else teamB.add(n);


            }
        }

        return (g.size() - teamA.size()) * teamA.size();
    }

    static Map<String, List<String>> input(String path) throws IOException {
        var g = new HashMap<String, List<String>>();
        Files.readAllLines(Path.of(path)).stream().forEach(line -> {
            var parts = line.split(": ");
            var n1 = parts[0];
            if (!g.containsKey(n1)) g.put(n1, new ArrayList<>());
            for (String n2 : parts[1].split(" ")) {
                if (!g.containsKey(n2)) g.put(n2, new ArrayList<>());
                g.get(n1).add(n2);
                g.get(n2).add(n1);
            }
        });

        return g;
    }

    static int paths(Map<String, List<String>> graph, String from, String to) {
        var res = 0;
        var visited = new HashSet<String>();
        visited.add(from);

        for (var a2: graph.get(from)) {
            var p = path(graph, new HashSet<>(visited), a2, to);
            if (p.isPresent()) {
                visited.addAll(p.get());
                visited.remove(to);
                res++;
            }
        }

        return res;
    }

    static Optional<List<String>> path(Map<String, List<String>> graph, Set<String> visited, String from, String to) {
        Queue<List<String>> q = new LinkedList<>();

        q.add(new LinkedList<>(List.of(from)));
        while (!q.isEmpty()) {
            var curr = q.poll();
            var head = curr.get(curr.size() - 1);
            if (head.equals(to)) return Optional.of(curr);

            var next = graph.get(head).stream()
                    .filter(a -> !visited.contains(a))
                    .map(a -> {
                        visited.add(a);
                        var l = new LinkedList<>(curr);
                        l.add(a);
                        return l;
                    })
                    .toList();

            q.addAll(next);
        }

        return Optional.empty();
    }
}
