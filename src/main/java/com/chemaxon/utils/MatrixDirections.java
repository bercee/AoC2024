package com.chemaxon.utils;

public class MatrixDirections {
    public static final Point2D UP = new Point2D(-1, 0);
    public static final Point2D DOWN = new Point2D(1, 0);
    public static final Point2D LEFT = new Point2D(0, -1);
    public static final Point2D RIGHT = new Point2D(0, 1);
    public static final Point2D UP_RIGHT = new Point2D(-1, 1);
    public static final Point2D UP_LEFT = new Point2D(-1, -1);
    public static final Point2D DOWN_RIGHT = new Point2D(1, 1);
    public static final Point2D DOWN_LEFT = new Point2D(1, -1);

    public static final Point2D[] MAIN_DIRECTIONS = {UP, DOWN, LEFT, RIGHT};
    public static final Point2D[] CROSS_DIRECTIONS = {UP_LEFT, UP_RIGHT, DOWN_RIGHT, DOWN_LEFT};
    public static final Point2D[] ALL_DIRECTIONS = {UP, DOWN, LEFT, RIGHT, UP_RIGHT, DOWN_RIGHT, DOWN_LEFT, UP_LEFT};
}
