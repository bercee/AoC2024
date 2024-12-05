package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Day5 extends Solver {
    record Pair(int i1, int i2) {
    }

    List<Pair> pairs = new ArrayList<>();
    List<List<Integer>> prints = new ArrayList<>();

    public Day5(List<String> input) {
        super(input);

        boolean pairs = true;
        for (String line : input) {
            if (line.trim().isEmpty()) {
                pairs = false;
                continue;
            }
            if (pairs) {
                var data = line.split("\\|");
                this.pairs.add(new Pair(Integer.parseInt(data[0]), Integer.parseInt(data[1])));
            } else {
                var data = line.split(",");
                this.prints.add(Arrays.stream(data).map(Integer::parseInt).collect(Collectors.toList()));
            }
        }
    }

    @Override
    public String solvePart1() {
        var validPrints = prints.stream().filter(p -> !isWrong(p)).toList();
        return convert(sumMiddles(validPrints));
    }

    @Override
    public String solvePart2() {
        var validPrints = new ArrayList<List<Integer>>();
        for (List<Integer> print : prints) {
            if (isWrong(print)) {
                var newList = new ArrayList<Integer>(print);
                do {
                    var wrongPair = getFirstWrongPair(newList);
                    swap(newList, wrongPair.get().i1(), wrongPair.get().i2());
                } while (isWrong(newList));
                validPrints.add(newList);
            }
        }
        return convert(sumMiddles(validPrints));
    }

    private Optional<Pair> getFirstWrongPair(List<Integer> list) {
        return pairs.stream().filter(p -> {
            return list.contains(p.i1()) && list.contains(p.i2()) && list.indexOf(p.i1()) > list.indexOf(p.i2());
        }).findFirst();
    }

    private boolean isWrong(List<Integer> list) {
        return getFirstWrongPair(list).isPresent();
    }


    private void swap(List<Integer> pair, int i1, int i2) {
        var idx1 = pair.indexOf(i1);
        var idx2 = pair.indexOf(i2);
        if (idx1 > -1 && idx2 > -1) {
            pair.set(idx1, i2);
            pair.set(idx2, i1);
        }
    }

    private int sumMiddles(List<List<Integer>> validPrints) {
        return validPrints.stream().mapToInt(print -> print.get(print.size() / 2)).sum();
    }
}
