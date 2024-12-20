package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.Maze;
import com.chemaxon.utils.Point2D;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.stream.Collectors;

import static com.chemaxon.utils.MatrixDirections.MAIN_DIRECTIONS;

public class Day20 extends Solver {

    static class OrderedPair {
        Point2D p1;
        Point2D p2;

        OrderedPair(Point2D p1, Point2D p2) {
            var list = new ArrayList<>(List.of(p1, p2));
            list.sort((x1, x2) -> {
                var c1 = Integer.compare(x1.i(), x2.i());
                var c2 = Integer.compare(x1.j(), x2.j());
                return c1 == 0 ? c2 : c1;
            });
            this.p1 = list.get(0);
            this.p2 = list.get(1);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OrderedPair obj2) {
                return obj2.p1.equals(p1) && obj2.p2.equals(p2);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(p1, p2);
        }
    }

    Point2D start;
    Point2D end;
    String[][] matrix;

    public Day20(List<String> input) {
        super(input);
        matrix = Parsers.createStringArrays(input);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j].equals("S")) {
                    start = new Point2D(i, j);
                    matrix[i][j] = ".";
                } else if (matrix[i][j].equals("E")) {
                    end = new Point2D(i, j);
                    matrix[i][j] = ".";
                }
            }
        }

//        LOGGER.info("start {} end {} ", start, end);
    }

    String[][] copyMatrixAndFreeWall(Point2D p) {
        var copy = Arrays.stream(matrix).map(String[]::clone).toArray(String[][]::new);
        copy[p.i()][p.j()] = ".";
        return copy;
    }

    Graph<Point2D, DefaultEdge> createGraph(String[][] matrix) {
        var maze = new Maze(matrix);
        var graph = new SimpleGraph<Point2D, DefaultEdge>(DefaultEdge.class);
        maze.filterByPosition(maze::isFree).forEach(graph::addVertex);
        graph.vertexSet().forEach(p -> Arrays.stream(MAIN_DIRECTIONS).map(p::add).filter(maze::isFree).forEach(pp -> graph.addEdge(p, pp)));
//        LOGGER.info("vertexes {} edges {}", graph.vertexSet().size(), graph.edgeSet().size());

        return graph;
    }

    GraphPath<Point2D, DefaultEdge> getShortestPath(Graph<Point2D, DefaultEdge> graph) {
        DijkstraShortestPath<Point2D, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
        return dijkstra.getPath(start, end);
    }

    int potentialSave(Point2D p, int origLength) {
        var modifiedMatrix = copyMatrixAndFreeWall(p);
        var graph = createGraph(modifiedMatrix);
        var path = getShortestPath(graph);
        return origLength - path.getLength();
    }

    @Override
    public String solvePart1() {
        var shortestOrig = getShortestPath(createGraph(matrix));
        var shortestVertexSet = new HashSet<>(shortestOrig.getVertexList());
        var walls = new Maze(matrix)
                .filterByValue("#"::equals).stream()
                .filter(w -> w.map(Arrays.asList(MAIN_DIRECTIONS)).stream()
                        .anyMatch(shortestVertexSet::contains)).collect(Collectors.toSet());
        var minSave = matrix.length < 100 ? 1 : 100;
        return String.valueOf(walls.stream().filter(w -> potentialSave(w, shortestOrig.getLength()) >= minSave).count());
    }

    @Override
    public String solvePart2() {
        var graph = createGraph(matrix);
        DijkstraShortestPath<Point2D, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
        var pathsFromEnd = dijkstra.getPaths(end);
        var pathsFromStart = dijkstra.getPaths(start);
        var freeCells =   new Maze(matrix).filterByValue("."::equals);

        Map<Point2D, Integer> distancesFromEnd = new HashMap<>();
        Map<Point2D, Integer> distancesFromStart = new HashMap<>();

        freeCells.forEach(p -> distancesFromEnd.put(p, pathsFromEnd.getPath(p).getLength()));
        freeCells.forEach(p -> distancesFromStart.put(p, pathsFromStart.getPath(p).getLength()));



        var shortestOrig = getShortestPath(createGraph(matrix));
        var shortestOrigLength = shortestOrig.getLength();
        int minSave = matrix.length < 100 ? 50 : 100;
        LOGGER.info("min save {}", minSave);

        Set<OrderedPair> ret = new HashSet<>();

        for (Point2D p1 : freeCells) {
            for (Point2D p2 : freeCells) {
                var l1 = p1.subtract(p2).L1();
                if (l1 > 20) {
                    continue;
                }

                var total = distancesFromStart.get(p1) + distancesFromEnd.get(p2) + l1;
                var save = shortestOrigLength - total;
                if (save >= minSave) {
                    ret.add(new OrderedPair(p1, p2));
                }
            }
        }

        return String.valueOf(ret.size());
    }
}
