package com.chemaxon.solvers.year2020;

import com.chemaxon.Solver;

import java.util.List;

public class Day1 extends Solver {

    private List<Integer> nums;

    public Day1(List<String> input) {
        super(input);
        nums = this.getInput().stream().map(Integer::parseInt).toList();
    }

    @Override
    public String solvePart1() {
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
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < j; k++) {
                    var ii = nums.get(i);
                    var jj = nums.get(j);
                    var kk = nums.get(k);
                    if (ii + jj + kk == 2020) {
                        return Integer.toString(ii * jj * kk);
                    }
                }
            }
        }
        return "";
    }
}
