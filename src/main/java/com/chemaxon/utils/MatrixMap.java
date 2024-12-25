package com.chemaxon.utils;

import java.lang.reflect.Array;
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

    public Set<Point2D> filterByValue(Predicate<? super T> predicate) {
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

    public Set<Point2D> filterByPosition(Predicate<Point2D> predicate) {
        Set<Point2D> ret = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var p = new Point2D(i, j);
                if (predicate.test(p)) {
                    ret.add(p);
                }
            }
        }
        return ret;
    }

    public List<Point2D> getNeighhours(Point2D p, Point2D[] directions) {
        return Arrays.stream(directions).map(p::add).filter(this::isInside).toList();
    }

    public List<T> getRow(int row) {
        if (row < 0 || row >= height) {
            return null;
        }

        return Arrays.stream(matrix[row]).toList();
    }

    public List<T> getColumn(int column) {
        if (column < 0 || column >= width) {
            return null;
        }


        List<T> list = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            list.add(matrix[i][column]);
        }

        return list;

    }
}
