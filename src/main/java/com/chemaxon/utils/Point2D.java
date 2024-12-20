package com.chemaxon.utils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record Point2D(int i, int j) {

    public Point2D add(Point2D other) {
        return new Point2D(i + other.i, j + other.j);
    }

    @Override
    public Point2D clone() {
        return new Point2D(i, j);
    }

    public int L1() {
        return Math.abs(i) + Math.abs(j);
    }

    public Point2D subtract(Point2D other) {
        return new Point2D(i - other.i, j - other.j);
    }

    public Point2D multiply(int scalar) {
        return new Point2D(i * scalar, j * scalar);
    }

    public Point2D rotateClockwise90() {
        return new Point2D(j, -i);
    }

    public Point2D rotate180() {
        return new Point2D(-i, -j);
    }

    public Point2D rotateCounterClockwise90() {
        return new Point2D(-j, i);
    }

    public Set<Point2D> map(Collection<Point2D> deltas) {
        return deltas.stream().map(this::add).collect(Collectors.toSet());
    }
}
