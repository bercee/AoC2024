package com.chemaxon.playground;

import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import com.chemaxon.utils.Point2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Playground {

    public static void main(String[] args) {
        int n = 5;
        for (int i = -15; i < 15; i++) {
            System.out.println(((i % n) + n) % n);
        }
    }
}
