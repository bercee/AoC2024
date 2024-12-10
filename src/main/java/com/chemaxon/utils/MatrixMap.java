package com.chemaxon.utils;

import java.util.*;
import java.util.function.Predicate;

public class MatrixMap<T> {
    private final T[][] matrix;
    public final int width;
    public final int height;

    public MatrixMap(T[][] matrix) {
        this.matrix = matrix;
        this.height = matrix.length;
        this.width = matrix[0].length;
    }


    public boolean isInside(int x, int y) {
        return x >= 0 && x < height && y >= 0 && y < width;
    }

    public boolean isInside(Point2D p) {
        return isInside(p.i(), p.j());
    }

    public T get(int x, int y) {
        return isInside(x, y) ? this.matrix[x][y] : null;
    }

    public T get(Point2D p) {
        return get(p.i(), p.j());
    }

    public Set<Point2D> filter(Predicate<? super T> predicate) {
        Set<Point2D> ret = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (predicate.test(get(i, j))) {
                    ret.add(new Point2D(i, j));
                }
            }
        }

        return ret;
    }
}
