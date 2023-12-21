package day20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {


    public static void main(String[] args) throws IOException {
        var res1 = part1("./src/day20/input.txt");
        System.out.println(res1);
    }

    static long part1(String path) throws IOException {
        var modules = parseModules(path);

        var low = 0L;
        var high = 0L;
        for (int i = 0; i < 1000; i++) {
            var res = play(modules, new Signal("button", SignalType.low, "broadcaster"));
            low += res[0];
            high += res[1];
        }
        return low * high;
    }

    static Map<String, Object> parseModules(String path) throws IOException {
        var map = new HashMap<String, Object>();
        for (var line : Files.readAllLines(Path.of(path))) {
            var parts = line.split(" -> ");
            var to = Arrays.stream(parts[1].split(", ")).toList();
            if (parts[0].equals("broadcaster")) map.put("broadcaster", new Broadcaster(to));
            else if (parts[0].startsWith("%")) map.put(parts[0].substring(1), new FlipFlop(SignalType.low, to));
            else if (parts[0].startsWith("&")) map.put(parts[0].substring(1), new Conjunction(new HashMap<>(), to));
            else throw new Error("invalid input");
        }

        map.entrySet().stream()
                .filter(e -> e.getValue() instanceof Conjunction)
                .forEach(entry -> {
                    Map<String, SignalType> from = map.entrySet()
                            .stream()
                            .filter(e2 -> {
                                var v = e2.getValue();
                                if (v instanceof Broadcaster) return ((Broadcaster) v).to.contains(entry.getKey());
                                else if (v instanceof FlipFlop) return ((FlipFlop) v).to.contains(entry.getKey());
                                else if (v instanceof Conjunction) return ((Conjunction) v).to.contains(entry.getKey());
                                else throw new Error("unknown module");
                            })
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toMap(a -> a, a -> SignalType.low));

                    map.put(entry.getKey(), ((Conjunction) entry.getValue()).withRecent(from));
                });

        return map;
    }

    static long[] play(Map<String, Object> modules, Signal init) {
        Queue<Signal> q = new LinkedList<>();
        q.add(init);
        var low = 0L;
        var high = 0L;
        while (!q.isEmpty()) {
            var s = q.poll();

            if (s.type == SignalType.low) low++;
            else high++;

            q.addAll(next(modules, s));
        }

        return new long[]{low, high};
    }

    static List<Signal> next(Map<String, Object> modules, Signal signal) {
        if (!modules.containsKey(signal.to)) return List.of();
        var module = modules.get(signal.to);

        if (module instanceof Broadcaster) {
            return ((Broadcaster) module).to.stream().map(dst -> signal.from(signal.to).to(dst)).toList();
        } else if (module instanceof FlipFlop) {
            if (signal.type == SignalType.high) return List.of();
            else {
                var f = (FlipFlop) module;
                modules.put(signal.to, f.flip());
                return f.to.stream().map(dst -> signal.from(signal.to).to(dst).with(f.recent.opposite())).toList();
            }
        } else if (module instanceof Conjunction c) {
            c.recent.put(signal.from, signal.type);
            var newSignal = c.recent.values().stream().allMatch(s -> s == SignalType.high) ? SignalType.low : SignalType.high;
            return c.to.stream().map(dst -> signal.from(signal.to).to(dst).with(newSignal)).toList();
        }

        throw new Error("unknown module");
    }

    record Broadcaster(List<String> to) {
    }

    record FlipFlop(SignalType recent, List<String> to) {
        FlipFlop flip() {
            return new FlipFlop(recent.opposite(), to);
        }
    }

    record Conjunction(Map<String, SignalType> recent, List<String> to) {
        Conjunction withRecent(Map<String, SignalType> recent) {
            return new Conjunction(recent, this.to);
        }
    }

    enum SignalType {
        low, high;

        SignalType opposite() {
            return this == low ? high : low;
        }
    }

    record Signal(String from, SignalType type, String to) {
        Signal from(String from) {
            return new Signal(from, this.type, this.to);
        }

        Signal to(String to) {
            return new Signal(this.from, this.type, to);
        }

        Signal with(SignalType type) {
            return new Signal(this.from, type, this.to);
        }
    }
}
