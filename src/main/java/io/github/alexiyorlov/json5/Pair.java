package io.github.alexiyorlov.json5;

public class Pair<ONE, TWO> {

    private final ONE first;
    private final TWO second;

    public Pair(ONE first, TWO second) {
        this.first = first;
        this.second = second;
    }

    public ONE getFirst() {
        return first;
    }

    public TWO getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
