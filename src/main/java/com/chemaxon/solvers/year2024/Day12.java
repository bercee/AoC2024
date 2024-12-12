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
            var corners = getCorners();
            var doubleCorners = getDoubleCorners();
            LOGGER.info("region {} corners {} double c {}", letter, corners.size(), doubleCorners.size());
            return corners.size() + doubleCorners.size() * 2;
        }

        private Set<Point2D> getCorners() {
            return points.stream()
                    .flatMap(p -> Arrays.stream(CHECK_DIRS).map(check -> check.add(p)))
                    .filter(p -> Arrays.stream(CORNER_DIRS).map(p::add).filter(points::contains).count() % 2 == 1)
                    .collect(Collectors.toSet());
        }

        private Set<Point2D> getDoubleCorners() {
            Set<Point2D> corners = new HashSet<>();
            for (Point2D point : points) {
                for (Point2D check : Arrays.stream(CHECK_DIRS).map(point::add).toList()) {
                    var cornerPoints = Arrays.stream(CORNER_DIRS).map(check::add).toList();
                    var isContaineds = cornerPoints.stream().map(this.points::contains).toList();
                    var isContainedCount = isContaineds.stream().filter(b -> b).count();
                    if (isContainedCount == 2) {
                        var idx1 = isContaineds.indexOf(true);
                        var idx2 = isContaineds.lastIndexOf(true);
                        var p1 = cornerPoints.get(idx1);
                        var p2 = cornerPoints.get(idx2);

                        var mainNeighbours = Arrays.stream(MatrixDirections.MAIN_DIRECTIONS).map(p1::add).toList();
                        if (!mainNeighbours.contains(p2)) {
                            LOGGER.info("tricky {} {}", letter, p1);
                            corners.add(check);
                        }
                    }
                }
            }

            return corners;
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
