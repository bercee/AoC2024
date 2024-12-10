package com.chemaxon.playground;

import com.chemaxon.utils.Point2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Playground {

    public static void main(String[] args) {
        List<Point2D> l1 = new ArrayList<>();
        List<Point2D> l2 = new ArrayList<>();

        l1.add(new Point2D(0, 0));
        l1.add(new Point2D(0, 1));

        l2.add(new Point2D(0, 0));
        l2.add(new Point2D(0, 1));

        System.out.println(l1.equals(l2));
    }
}
