package com.chemaxon.utils;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.geom.AffineTransform;

public class Directions {
    public static final Vector2D IDENTITY = Vector2D.ZERO;
    public static final Vector2D UP = new Vector2D(-1, 0);
    public static final Vector2D DOWN = new Vector2D(1, 0);
    public static final Vector2D LEFT = new Vector2D(0, -1);
    public static final Vector2D RIGHT = new Vector2D(0, 1);
    public static final Vector2D UP_LEFT = new Vector2D(-1, -1);
    public static final Vector2D UP_RIGHT = new Vector2D(-1, 1);
    public static final Vector2D DOWN_LEFT = new Vector2D(1, -1);
    public static final Vector2D DOWN_RIGHT = new Vector2D(1, 1);
    public static final Vector2D[] MAIN_DIRECTIONS = {UP, DOWN, LEFT, RIGHT};
    public static final Vector2D[] CROSSED_DIRECTIONS = {UP_LEFT, DOWN_LEFT, UP_RIGHT, DOWN_RIGHT};
    public static final Vector2D[] HORIZONTAL_DIRECTIONS = {LEFT, RIGHT};
    public static final Vector2D[] VERTICAL_DIRECTIONS = {DOWN, UP};
    public static final Vector2D[] ALL_DIRECTIONS = {UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};
    public static final Vector2D[] ALL_DIRECTIONS_WITH_IDENTITY = {UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT, IDENTITY};

    public static final AffineTransform ROTATE_IDENTITY = new AffineTransform();
    public static final AffineTransform ROTATE_CLOCKWISE_90 = AffineTransform.getRotateInstance(Math.toRadians(90));
    public static final AffineTransform ROTATE_180 = AffineTransform.getRotateInstance(Math.toRadians(180));
    public static final AffineTransform ROTATE_ANTICLOCKWISE_270 = AffineTransform.getRotateInstance(Math.toRadians(270));
    public static final AffineTransform[] ROTATIONS = {ROTATE_IDENTITY, ROTATE_CLOCKWISE_90, ROTATE_180, ROTATE_ANTICLOCKWISE_270};
}
