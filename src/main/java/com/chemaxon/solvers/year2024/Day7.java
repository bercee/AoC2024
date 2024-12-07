package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;

import java.util.ArrayList;
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
                .filter(t -> generateCombinations(t.nums.size() - 1, 2).stream()
                        .anyMatch(combinations -> evalCombination(t.value, t.nums, combinations)))
                .mapToLong(t -> t.value).sum());
    }

    @Override
    public String solvePart2() {
        return convert(tests.stream()
                .filter(t -> generateCombinations(t.nums.size() - 1, 3).stream()
                        .anyMatch(combinations -> evalCombination(t.value, t.nums, combinations)))
                .mapToLong(t -> t.value).sum());
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


    private List<Operation[]> generateCombinations(int n, int ops) {
        List<Operation[]> result = new ArrayList<>();
        Operation[] combination = new Operation[n];
        backtrack(result, combination, n, ops, 0);
        return result;
    }

    private void backtrack(List<Operation[]> result, Operation[] combination, int n, int ops, int index) {
        if (index == n) {
            result.add(combination.clone());
            return;
        }
        for (int value = 0; value < ops; value++) {
            combination[index] = value == 0 ? Operation.ADD : value == 1 ? Operation.MUL : Operation.CONCAT;
            backtrack(result, combination, n, ops, index + 1);
        }
    }
}
