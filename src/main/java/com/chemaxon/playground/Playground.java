package com.chemaxon.playground;

import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import com.chemaxon.utils.Point2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;

public class Playground {

    public static void main(String[] args) {
        Map<String, Integer> bag = new HashMap<>();
        bag.merge("A", 1, Integer::sum);
        bag.merge("B", 1, Integer::sum);
        bag.merge("A", 1, Integer::sum);
        System.out.println(bag);
    }


}
