package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 extends Solver {

    List<int[]> rows;

    public Day2(List<String> input) {
        super(input);
        rows = input.stream().map(s -> s.split("\\s+")).map(s -> Arrays.stream(s).mapToInt(Integer::parseInt).toArray()).collect(Collectors.toList());
    }

    @Override
    public String solvePart1() {
        return convert(rows.stream().filter(this::isSafe).count());
    }

    @Override
    public String solvePart2() {
        int count = 0;
        for (int i = 0; i < rows.size(); i++) {
            var row = rows.get(i);
            if (isSafe(row)) {
                count++;
            } else {
                for (int j = 0; j < row.length; j++) {
                    var copy = ArrayUtils.remove(row, j);
                    if (isSafe(copy)) {
                        count++;
                        break;
                    }
                }
            }
        }
        return convert(count);
    }

    private boolean isSafe(int[] row) {
        var sign = Math.signum(row[0] - row[1]);
        for (int j = 0; j < row.length - 1; j++) {
            if (Math.signum(row[j] - row[j + 1]) != sign) {
                return false;
            }

            if (Math.abs(row[j] - row[j + 1]) > 3 || Math.abs(row[j] - row[j + 1]) < 1) {
                return false;
            }
        }

        return true;
    }
}
