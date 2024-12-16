package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixDirections;
import com.chemaxon.utils.Point2D;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.chemaxon.utils.MatrixDirections.MAIN_DIRECTIONS;

public class Day16 extends Solver {

    record State(Point2D point, Point2D dir) {
        @Override
        public boolean equals(Object o) {
            if (o instanceof State) {
                return point.equals(((State) o).point) && dir.equals(((State) o).dir);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, dir);
        }
    }

    Point2D start;
    Point2D end;
    Point2D startDir = MatrixDirections.RIGHT;

    Graph<State, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);


    public Day16(List<String> input) {
        super(input);
        var matrix = Parsers.createStringArrays(input);

        //read maze
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

        //add vertixes
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j].equals(".")) {
                    for (Point2D dir : MAIN_DIRECTIONS) {
                        graph.addVertex(new State(new Point2D(i, j), dir));
                    }
                }
            }
        }

        //add turning edges
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j].equals(".")) {
                    var p = new Point2D(i, j);
                    for (Point2D dir : MAIN_DIRECTIONS) {
                        var turn1 = dir.rotateClockwise90();
                        var turn2 = dir.rotateCounterClockwise90();
                        graph.setEdgeWeight(graph.addEdge(new State(p, dir), new State(p, turn1)), 1000);
                        graph.setEdgeWeight(graph.addEdge(new State(p, dir), new State(p, turn2)), 1000);
                    }
                }
            }
        }

        //add moving edges
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {

                if (matrix[i][j].equals(".")) {
                    var p = new Point2D(i, j);
                    for (Point2D dir : MAIN_DIRECTIONS) {
                        var next = dir.add(p);
                        if (matrix[next.i()][next.j()].equals(".")) {
                            graph.setEdgeWeight(graph.addEdge(new State(p, dir), new State(next, dir)), 1);
                        }
                    }
                }
            }
        }

        LOGGER.info("vertixes: {} edges: {}", graph.vertexSet().size(), graph.edgeSet().size());

    }

    int getBestWeight() {
        var startState = new State(start, startDir);
        var paths = new ArrayList<>(Arrays.stream(MAIN_DIRECTIONS).map(endDir -> {
            var endState = new State(end, endDir);
            DijkstraShortestPath<State, DefaultWeightedEdge> dijkstraShortestPath
                    = new DijkstraShortestPath<>(graph);
            return dijkstraShortestPath
                    .getPath(startState, endState);
        }).toList());
        paths.sort(Comparator.comparingDouble(GraphPath::getWeight));
        LOGGER.info("best path lengh: {} and weight: {}", paths.getFirst().getLength(), paths.getFirst().getWeight());
        return (int) paths.getFirst().getWeight();
    }

    @Override
    public String solvePart1() {
        return convert(getBestWeight());
    }


    @Override
    public String solvePart2() {
        var startState = new State(start, startDir);

        var bestWeight = getBestWeight();
        Set<State> coveredPaths = new HashSet<>();

        for (Point2D endDir : MAIN_DIRECTIONS) {
            var endState = new State(end, endDir);
            List<GraphPath<State, DefaultWeightedEdge>> ret = new ArrayList<>();

            DijkstraShortestPath<State, DefaultWeightedEdge> forwards
                    = new DijkstraShortestPath<>(graph);
            var forwardPaths = forwards.getPaths(startState);

            DijkstraShortestPath<State, DefaultWeightedEdge> backwards = new DijkstraShortestPath<>(graph);
            var backwardPaths = backwards.getPaths(endState);


            for (State state : graph.vertexSet()) {
                if (state.equals(startState) || state.equals(endState)) {
                    continue;
                }

                var forwardP = forwardPaths.getPath(state);
                var backwardP = backwardPaths.getPath(state);

                var forwardHasNew = forwardP.getVertexList().stream().anyMatch(vertex -> !coveredPaths.contains(vertex));
                var backwardHasNew = backwardP.getVertexList().stream().anyMatch(vertex -> !coveredPaths.contains(vertex));
                if (!forwardHasNew && !backwardHasNew) {
                    continue;
                }

                var forwardW = forwardP.getWeight();
                var backwardW = backwardP.getWeight();

                if (forwardW + backwardW == bestWeight) {
                    coveredPaths.addAll(forwardP.getVertexList());
                    coveredPaths.addAll(backwardP.getVertexList());
                }
            }
        }

        return convert(coveredPaths.stream().map(p -> p.point).collect(Collectors.toSet()).size());
    }

}
