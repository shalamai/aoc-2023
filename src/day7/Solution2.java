package day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Solution2 {

    public static void main(String[] args) throws IOException {
        var res1 = part2("./src/day7/input.txt");
        System.out.println(res1);
    }


    static int part2(String path) throws IOException {
        var hands = input(path);
        hands.sort(handsComparator);
        int acc = 0;

        for (int i = 0; i < hands.size(); i++) {
            acc += (i + 1) * hands.get(i).bid;
        }

        return acc;
    }

    static List<Hand2> input(String path) throws IOException {
        return Files.readAllLines(Path.of(path))
                .stream()
                .map(l -> {
                    var parts = l.split(" ");
                    return new Hand2(parts[0], Integer.parseInt(parts[1]));
                })
                .collect(Collectors.toList());
    }

    static Comparator<Hand2> handsComparator = (a, b) -> {
        if (a.type != b.type) return a.type - b.type;
        else for (int i = 0; i < a.cards.size(); i++) {
            if (!Objects.equals(a.cards.get(i), b.cards.get(i))) return a.cards.get(i) - b.cards.get(i);
        }

        return 0;
    };
}

class Hand2 {
    List<Integer> cards;
    int bid;

    int type;

    private Map<Character, Integer> code2int = Map.of(
            'T', 10,
            'J', 1,
            'Q', 12,
            'K', 13,
            'A', 14
    );

    private List<String> combPrefixes = List.of("5", "4", "32", "3", "22", "2", "1");

    Hand2(String cards, int bid) {
        this.cards = cards
                .chars()
                .map(c -> Character.isDigit(c)
                        ? Integer.parseInt(String.valueOf((char) c))
                        : code2int.get((char) c)
                )
                .boxed()
                .collect(Collectors.toList());
        this.bid = bid;
        this.determineType();
    }

    void determineType() {
        var combination = this.cards.stream()
                .filter(c -> c != 1)
                .collect(Collectors.groupingBy(c -> c))
                .values()
                .stream()
                .map(List::size)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        var jokers = (int) this.cards.stream().filter(c -> c == 1).count();
        if (jokers == 5) {
            combination.add(5);
        } else {
            combination.set(0, combination.get(0) + jokers);
        }


        var combination2 = combination.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        for (int i = 0; i < combPrefixes.size(); i++) {
            if (combination2.startsWith(combPrefixes.get(i))) {
                this.type = combPrefixes.size() - i;
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "Hand{" +
                "cards=" + cards +
                ", type=" + type +
                '}';
    }
}
