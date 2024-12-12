package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixDirections;
import com.chemaxon.utils.MatrixMap;
import com.chemaxon.utils.Point2D;

import java.util.*;
import java.util.stream.Collectors;

public class Day12 extends Solver {

    record Region(String letter, Set<Point2D> points) {
        static Point2D[] CORNER_DIRS = new Point2D[]{MatrixDirections.IDENTITY, MatrixDirections.UP, MatrixDirections.LEFT, MatrixDirections.UP_LEFT};
        static Point2D[] CHECK_DIRS = new Point2D[]{MatrixDirections.IDENTITY, MatrixDirections.DOWN, MatrixDirections.RIGHT, MatrixDirections.DOWN_RIGHT};

        int getArea() {
            return points.size();
        }

        int getPerim() {
            int count = 0;
            for (Point2D p : points) {
                count += (int) Arrays.stream(MatrixDirections.MAIN_DIRECTIONS).map(p::add).filter(pp -> !points.contains(pp)).count();
            }

            return count;
        }

        int getSides() {
            return getPossibleCorners().stream().mapToInt(p -> isSingleCorner(p) ? 1 : isDoubleCorner(p) ? 2 : 0).sum();
        }

        private boolean isSingleCorner(Point2D p) {
            return Arrays.stream(CORNER_DIRS).map(p::add).filter(points::contains).count() % 2 == 1;
        }

        private boolean isDoubleCorner(Point2D p) {
            var cornerPoints = Arrays.stream(CORNER_DIRS).map(p::add).toList();
            var isContaineds = cornerPoints.stream().map(this.points::contains).toList();
            var isContainedCount = isContaineds.stream().filter(b -> b).count();
            if (isContainedCount == 2) {
                var idx1 = isContaineds.indexOf(true);
                var idx2 = isContaineds.lastIndexOf(true);
                var p1 = cornerPoints.get(idx1);
                var p2 = cornerPoints.get(idx2);

                var mainNeighbours = Arrays.stream(MatrixDirections.MAIN_DIRECTIONS).map(p1::add).toList();
                return !mainNeighbours.contains(p2);
            }

            return false;

        }

        private Set<Point2D> getPossibleCorners() {
            return points.stream().flatMap(p -> Arrays.stream(CHECK_DIRS).map(p::add)).collect(Collectors.toSet());
        }


    }

    MatrixMap<String> map;
    List<Region> regions = new ArrayList<>();


    public Day12(List<String> input) {
        super(input);
        map = new MatrixMap<>(Parsers.createStringArrays(input));
        fillRegions();

    }

    void fillRegions() {
        for (int i = 0; i < map.height; i++) {
            for (int j = 0; j < map.width; j++) {
                var p = new Point2D(i, j);
                if (isUnstored(p)) {
//                    LOGGER.info("new region {} from {} ", map.get(p), p);
                    var r = new Region(map.get(p), new HashSet<>(List.of(p)));
                    regions.add(r);
                    var neighbours = getUnstoredSameNeighbors(p);
                    while (!neighbours.isEmpty()) {
                        r.points.addAll(neighbours);
                        neighbours = neighbours.stream().flatMap(pp -> getUnstoredSameNeighbors(pp).stream()).collect(Collectors.toSet());
                    }
                }
            }
        }
    }

    Set<Point2D> getUnstoredSameNeighbors(Point2D point) {
        var s = map.get(point);
        return map.getNeighhours(point, MatrixDirections.MAIN_DIRECTIONS).stream()
                .filter(p -> map.get(p).equals(s))
                .filter(this::isUnstored)
                .collect(Collectors.toSet());
    }

    boolean isUnstored(Point2D point) {
        return regions.stream().noneMatch(region -> region.points.contains(point));
    }

    @Override
    public String solvePart1() {
        return convert(regions.stream().mapToInt(r -> r.getArea() * r.getPerim()).sum());
    }

    @Override
    public String solvePart2() {
        return convert(regions.stream().mapToInt(r -> r.getArea() * r.getSides()).sum());
    }
}
