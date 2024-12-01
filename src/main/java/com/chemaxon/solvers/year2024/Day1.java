package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.List;

public class Day1 extends Solver {
    private RealMatrix matrix;

    public Day1(List<String> input) {
        super(input);
        matrix = Parsers.createMatrix(input);
    }

    @Override
    public String solvePart1() {
        var col0 = MatrixUtils.createRealVector(Arrays.stream(matrix.getColumn(0)).sorted().toArray());
        var col1 = MatrixUtils.createRealVector(Arrays.stream(matrix.getColumn(1)).sorted().toArray());
        var diff = col0.subtract(col1);
        return convertAsInt(diff.getL1Norm());
    }

    @Override
    public String solvePart2() {
        var ret = Arrays.stream(matrix.getColumn(0)).map(n -> n * Arrays.stream(matrix.getColumn(1)).filter(nn -> nn == n).count()).sum();
        return convertAsInt(ret);
    }
}
