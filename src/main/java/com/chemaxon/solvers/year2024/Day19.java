package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;

import java.util.*;
import java.util.stream.Collectors;

public class Day19 extends Solver {
    Set<String> patternSet;
    List<String> targets;

    Map<String, Long> memo = new HashMap<>();

    static class Num {
        long n = 0;
    }

    public Day19(List<String> input) {
        super(input);
        patternSet = new HashSet<>(Arrays.stream(input.getFirst().split(", ")).toList());
        targets = input.subList(2, input.size());

        //fill memo (run backtrack)
        targets.forEach(t -> this.backtrack(t, new Num()));
    }


    void backtrack(String target, Num found) {
        if (memo.containsKey(target)) {
            found.n += memo.get(target);
            return;
        }

        var alreadyFound = found.n;
        var promising = patternSet.stream().filter(target::startsWith).collect(Collectors.toSet());

        if (promising.isEmpty()) {
            memo.put(target, 0L);
            return;
        }

        for (String pattern : promising) {
            var remaining = target.substring(pattern.length());
            if (remaining.isEmpty()) {
                found.n++;
            } else {
                backtrack(remaining, found);
            }
        }

        memo.put(target, found.n - alreadyFound);
    }

    @Override
    public String solvePart1() {
        return String.valueOf(targets.stream().filter(t -> memo.getOrDefault(t, 0L) > 0).count());
    }

    @Override
    public String solvePart2() {
        return String.valueOf(targets.stream().mapToLong(t -> memo.getOrDefault(t, 0L)).sum());
    }
}
