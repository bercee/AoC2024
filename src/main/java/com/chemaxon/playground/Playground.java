package com.chemaxon.playground;

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
        Graph<Point2D, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        g.addVertex(new Point2D(0, 0));
        System.out.println(g.containsVertex(new Point2D(0, 0)));
    }
}
