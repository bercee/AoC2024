package com.chemaxon.solvers.year2020;

import com.chemaxon.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 extends Solver {

    public Day1(List<String> input) {
        super(input);
    }

    @Override
    public String solvePart1() {
        var nums = this.getInput().stream().map(Integer::parseInt).toList();
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (nums.get(i) + nums.get(j) == 2020) {
                    return Integer.toString(nums.get(i) * nums.get(j));
                }
            }
        }
        return "";
    }

    @Override
    public String solvePart2() {
        return "";
    }
}
