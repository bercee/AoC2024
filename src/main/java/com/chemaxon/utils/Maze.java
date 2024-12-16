package com.chemaxon.utils;


public class Maze extends MatrixMap<String> {
    public Maze(String[][] matrix) {
        super(matrix);
    }

    public boolean isFree(int i, int j) {
        return isInside(i, j) && get(i, j).equals(".");
    }

    public boolean isFree(Point2D p) {
        return isFree(p.i(), p.j());
    }
}
