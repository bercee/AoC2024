package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import com.chemaxon.utils.Point2D;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.chemaxon.utils.MatrixDirections.MAIN_DIRECTIONS;

public class Day18 extends Solver {
    int w;
    int h;
    int firstBatch;

    Point2D start;
    Point2D end;

    List<Point2D> blocks;
    List<Point2D> newBlocks;

    Graph<Point2D, DefaultEdge> graph;

    public Day18(List<String> input) {
        super(input);
        if (input.size() < 1000) { // test scenario
            w = 7;
            h = 7;
            firstBatch = 12;
        } else {
            w = 71;
            h = 71;
            firstBatch = 1024;
        }
        start = new Point2D(0, 0);
        end = new Point2D(w - 1, h - 1);

        var sublist = input.subList(0, firstBatch);

        var newSublist = input.subList(firstBatch, input.size());

        blocks = toPoints(sublist);
        newBlocks = toPoints(newSublist);
        createGraph();
        LOGGER.info("{} blocks, {} new blocks", blocks.size(), newBlocks.size());
        LOGGER.info("{} vertexes {} edges", graph.vertexSet().size(), graph.edgeSet().size());
//        print();
    }

    List<Point2D> toPoints(List<String> input) {
        return input.stream().map(s -> Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray()).map(d -> new Point2D(d[0], d[1])).collect(Collectors.toList());
    }

    private void createGraph() {
        graph = new SimpleGraph<>(DefaultEdge.class);
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                var p = new Point2D(i, j);
                if (!blocks.contains(p)) {
                    graph.addVertex(p);
                }
            }
        }

        for (Point2D p : graph.vertexSet()) {
            for (Point2D d : MAIN_DIRECTIONS) {
                var p2 = d.add(p);
                if (isInside(p2) && graph.vertexSet().contains(p2)) {
                    graph.addEdge(p, p2);
                }
            }
        }
//        LOGGER.info("vertexes {} edges {}", graph.vertexSet().size(), graph.edgeSet().size());
    }

    void print() {
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                var p = new Point2D(i, j);
                if (!blocks.contains(p)) {
                    System.out.print(".");
                } else {
                    System.out.print("#");
                }
            }
            System.out.println();
        }
    }


    void print(GraphPath<Point2D, DefaultEdge> path) {
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                var p = new Point2D(i, j);
                if (path.getVertexList().contains(p)) {
                    System.out.print("O");
                } else if (!blocks.contains(p)) {
                    System.out.print(".");
                } else {
                    System.out.print("#");
                }
            }
            System.out.println();
        }
    }

    boolean isInside(Point2D p) {
        return p.i() >= 0 && p.i() < w && p.j() >= 0 && p.j() < h;
    }

    int shortestPath() {
        DijkstraShortestPath<Point2D, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
        var path = dijkstra.getPath(start, end);
        return path == null ? -1 : path.getLength();
    }

    @Override
    public String solvePart1() {
        return convert(shortestPath());
    }

    @Override
    public String solvePart2() {
//        LOGGER.info("new sublist {}", newBlocks.size());
        Point2D ret = null;
        for (Point2D newBlock : newBlocks) {
            blocks.add(newBlock);
//            LOGGER.info("{}", newBlock);
            createGraph();
            if (shortestPath() == -1) {
                ret = newBlock;
                break;
            }

        }
        return ret == null ? "" : ret.i() + "," + ret.j();
    }
}
