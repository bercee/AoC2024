package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;

import java.util.Arrays;
import java.util.List;

public class Day7 extends Solver {
    enum Operation {
        ADD, MUL, CONCAT;
    }

    record Test(long value, List<Long> nums) {

    }

    List<Test> tests;

    public Day7(List<String> input) {
        super(input);
        tests = input.stream().map(l -> {
            var dat = l.split(":");
            var value = Long.parseLong(dat[0]);
            var nums = Arrays.stream(dat[1].trim().split("\\s+")).map(Long::parseLong).toList();
            return new Test(value, nums);
        }).toList();
    }

    @Override
    public String solvePart1() {
        return convert(tests.stream()
                .filter(t -> backtrack(t, new Operation[t.nums.size()-1], 2, 0))
                .mapToLong(t -> t.value).sum());
    }

    @Override
    public String solvePart2() {
        return convert(tests.stream()
                .filter(t -> backtrack(t, new Operation[t.nums.size()-1], 3, 0))
                .mapToLong(t -> t.value).sum());
    }

    private boolean backtrack(Test t, Operation[] combination, int ops, int index) {
        if (index == t.nums.size() - 1) {
            return evalCombination(t.value, t.nums, combination);
        }

        for (int value = 0; value < ops; value++) {
            combination[index] = value == 0 ? Operation.ADD : value == 1 ? Operation.MUL : Operation.CONCAT;
            boolean result = backtrack(t, combination, ops, index + 1);
            if (result) {
                return true;
            }
        }

        return false;
    }

    private boolean evalCombination(long value, List<Long> nums, Operation[] combination) {
        long sum = nums.getFirst();
        for (int i = 1; i < nums.size(); i++) {
            if (combination[i - 1] == Operation.ADD) {
                sum += nums.get(i);
            } else if (combination[i - 1] == Operation.MUL) {
                sum *= nums.get(i);
            } else if (combination[i - 1] == Operation.CONCAT) {
                sum = Long.parseLong(sum + "" + nums.get(i));
            }
        }
        return sum == value;
    }
}
