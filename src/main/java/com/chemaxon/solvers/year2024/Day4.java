package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class Day4 extends Solver {

    private static final String[] XMAS = {"X", "M", "A", "S"};
    private static final RealVector[] DIRS = new RealVector[]{
            new ArrayRealVector(new double[]{1, 0}),
            new ArrayRealVector(new double[]{-1, 0}),
            new ArrayRealVector(new double[]{0, 1}),
            new ArrayRealVector(new double[]{0, -1}),
            new ArrayRealVector(new double[]{1, 1}),
            new ArrayRealVector(new double[]{-1, -1}),
            new ArrayRealVector(new double[]{1, -1}),
            new ArrayRealVector(new double[]{-1, 1}),
    };


    private final String[][] matrix;
    private final int h;
    private final int w;

    public Day4(List<String> input) {
        super(input);

        matrix = Parsers.createStringArrays(input);
        h = matrix.length;
        w = matrix[0].length;
    }

    @Override
    public String solvePart1() {
        int count = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                for (RealVector dir : DIRS) {
                    if (isWord(i, j, dir)) {
                        count++;
                    }
                }
            }
        }
        return convert(count);
    }

    private boolean isWord(int i, int j, RealVector dir) {

        for (int k = 0; k < 4; k++) {
            int ii = i + (int) dir.getEntry(0) * k;
            int jj = j + (int) dir.getEntry(1) * k;
            if (isOutside(ii, jj)) {
                return false;
            }
            if (!matrix[ii][jj].equals(XMAS[k])) {
                return false;
            }
        }

        return true;
    }

    private boolean isOutside(int i, int j) {
        return i < 0 || j < 0 || i >= h || j >= w;
    }

    static int[] DIMS = {0, 1};
    static int[] ORIS = {-1, 1};

    @Override
    public String solvePart2() {
        int count = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (matrix[i][j].equals("A")) {
                    var c = new ArrayRealVector(new double[]{i, j});
                    boolean foundCross = false;
                    for (int dim : DIMS) {
                        if (foundCross) {
                            break;
                        }
                        for (int ori : ORIS) {
                            var m1 = c.copy();
                            var m2 = c.copy();
                            m1.addToEntry(dim, ori);
                            m2.addToEntry(dim, ori);
                            m1.addToEntry(flip(dim), 1);
                            m2.addToEntry(flip(dim), -1);

                            var s1 = c.copy();
                            var s2 = c.copy();
                            s1.addToEntry(dim, ori * -1);
                            s2.addToEntry(dim, ori * -1);
                            s1.addToEntry(flip(dim), 1);
                            s2.addToEntry(flip(dim), -1);


                            if (checkX_MAS(List.of(m1, m2, s1, s2))) {
                                count++;
                                foundCross = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return convert(count);
    }

    private int flip(int d) {
        return d == 0 ? 1 : 0;
    }

    private boolean checkX_MAS(List<RealVector> l) {
        for (RealVector realVector : l) {
            if (isOutside((int) realVector.getEntry(0), (int) realVector.getEntry(1))) {
                return false;
            }
        }

        return getChar(l.get(0)).equals("M") && getChar(l.get(1)).equals("M") && getChar(l.get(2)).equals("S") && getChar(l.get(3)).equals("S");

    }

    private String getChar(RealVector v) {
        if (isOutside((int) v.getEntry(0), (int) v.getEntry(1))) {
            return "";
        }
        return matrix[(int) v.getEntry(0)][(int) v.getEntry(1)];
    }
}
