package com.chemaxon.playground;

import org.apache.commons.geometry.euclidean.twod.Vector2D;
import java.util.Random;

public class Playground {

    public static void main(String[] args) {
        var v = Vector2D.of(new Random().nextDouble(), new Random().nextDouble());
        System.out.println(v.hashCode() == v.multiply(-1).hashCode()); //always prints true
    }
}
