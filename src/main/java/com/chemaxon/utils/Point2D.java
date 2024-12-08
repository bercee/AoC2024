package com.chemaxon.utils;

public record Point2D(int i, int j) {

    public Point2D add(Point2D other) {
        return new Point2D(i + other.i, j + other.j);
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
}
