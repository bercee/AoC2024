package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;

import java.util.*;
import java.util.stream.Collectors;

class MyBag<T> {
    HashMap<T, Long> map = new HashMap<>();

    void add(T t) {
        add(t, 1L);
    }

    void add(T t, long count) {
        if (map.containsKey(t)) {
            map.put(t, map.get(t) + count);
        } else {
            map.put(t, count);
        }
    }

    void addAll(Collection<T> c) {
        c.forEach(this::add);
    }

    Set<T> uniqueKeys() {
        return map.keySet();
    }

    long getCount(T t) {
        return map.get(t) != null ? map.get(t) : 0L;
    }

    long size() {
        return uniqueKeys().stream().mapToLong(t -> map.get(t)).sum();
    }


}

public class Day11 extends Solver {
    MyBag<Long> bag = new MyBag<>();

    public Day11(List<String> input) {
        super(input);
        bag.addAll(Arrays.stream(input.getFirst().split("\\s+")).mapToLong(Long::parseLong).boxed().collect(Collectors.toList()));
    }

    List<Long> transform(long n) {
        if (n == 0) {
            return List.of(1L);
        }
        var str = String.valueOf(n);
        if (str.length() % 2 == 1) {
            return List.of(n * 2024);
        }

        var str1 = str.substring(0, str.length() / 2);
        var str2 = str.substring(str.length() / 2);

        return List.of(Long.parseLong(str1), Long.parseLong(str2));
    }

    void blink2() {
        var newBag = new MyBag<Long>();
        for (Long n : bag.map.keySet()) {
            var count = bag.getCount(n);
            var result = transform(n);
            result.forEach(r -> newBag.add(r, count));
        }

        bag = newBag;
    }


    @Override
    public String solvePart1() {
        for (int i = 0; i < 25; i++) {
            blink2();
        }
        return convert(bag.size());
    }

    @Override
    public String solvePart2() {
        for (int i = 0; i < 75; i++) {
            blink2();
        }

        return convert(bag.size());
    }
}
