package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 extends Solver {

    record Nums(long x, long y) {
    }


    record SysInt(long[][] A, long[] b) {
        long[] solve() {
            double[][] Ad = Arrays.stream(A).map(aa -> Arrays.stream(aa).asDoubleStream().toArray()).toArray(double[][]::new);
            double[] bd = Arrays.stream(b).asDoubleStream().toArray();
            DecompositionSolver solver = new LUDecomposition(MatrixUtils.createRealMatrix(Ad)).getSolver();
            var x = solver.solve(new ArrayRealVector(bd));

            //need to convert back to long and check the solution
            long[] xInt = Arrays.stream(x.toArray()).mapToLong(d -> (long) Precision.round(d, 2)).toArray();

            int n = b.length;
            boolean ok = true;
            for (int i = 0; i < n; i++) {
                long sum = 0;
                for (int j = 0; j < n; j++) {
                    sum += xInt[j] * A[i][j];
                }
                if (sum != b[i]) {
                    ok = false;
                    break;
                }
            }


            return ok ? xInt : null;
        }
    }

    List<SysInt> sysInts = new ArrayList<>();

    static String PATTERN = "X[+=](\\d+),\\s+Y[+=](\\d+)";

    public Day13(List<String> input) {
        super(input);
    }

    Nums parse(String line) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return new Nums(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)));
        }

        return new Nums(0, 0);

    }

    void readInput(long add) {

        for (int i = 0; i < this.getInput().size(); i += 4) {
            var nums1 = parse(this.getInput().get(i));
            var nums2 = parse(this.getInput().get(i + 1));
            var nums3 = parse(this.getInput().get(i + 2));
            sysInts.add(new SysInt(new long[][]{{nums1.x, nums2.x}, {nums1.y, nums2.y}}, new long[]{nums3.x + add, nums3.y + add}));
        }
    }

    long solve() {
        long count = 0;

        for (SysInt sysInt : sysInts) {
            var ret = sysInt.solve();
            if (ret != null) {
                count += ret[0] * 3 + ret[1];
            }
        }
        return count;
    }


    @Override
    public String solvePart1() {
        readInput(0);
        return convert(solve());
    }

    @Override
    public String solvePart2() {
        readInput(10000000000000L);
        return convert(solve());
    }
}
