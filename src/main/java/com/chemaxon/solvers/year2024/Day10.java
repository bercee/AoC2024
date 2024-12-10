package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixDirections;
import com.chemaxon.utils.Point2D;

import java.util.*;
import java.util.stream.Collectors;

public class Day10 extends Solver {

    int[][] map;
    int h;
    int w;

    List<Point2D> startPoints = new ArrayList<>();


    public Day10(List<String> input) {
        super(input);

        map = Parsers.createIntArrays(input);
        h = map.length;
        w = map[0].length;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (map[i][j] == 0) {
                    startPoints.add(new Point2D(i, j));
                }
            }
        }

    }

    boolean isInside(Point2D p) {
        return p.i() >= 0 && p.i() < h && p.j() >= 0 && p.j() < w;
    }

    int get(Point2D p) {
        return map[p.i()][p.j()];
    }

    @Override
    public String solvePart1() {
        int count = 0;
        for (Point2D startPoint : startPoints) {
            Set<Point2D> targets = new HashSet<>();
            backtrack(targets, startPoint);
            count += targets.size();
        }
        return convert(count);
    }

    @Override
    public String solvePart2() {
        int count = 0;
        for (Point2D startPoint : startPoints) {
            Set<List<Point2D>> targets = new HashSet<>();
            List<Point2D> firstPath = new ArrayList<>();
            firstPath.add(startPoint);
            backtrack2(targets, firstPath);
            count += targets.size();
        }
        return convert(count);
    }

    void backtrack2(Set<List<Point2D>> paths, List<Point2D> currentPath) {
        var current = currentPath.getLast();
        if (get(current) == 9) {
            paths.add(new ArrayList<>(currentPath));
        } else {
            var neighbors = getValidNeighbors(current).stream().toList();
            for (Point2D neighbor : neighbors) {
                var nextPath = new ArrayList<>(currentPath);
                nextPath.add(neighbor);
                backtrack2(paths, nextPath);
            }
        }
    }

    void backtrack(Set<Point2D> targets, Point2D current) {
        if (get(current) == 9) {
            targets.add(current);
        } else {
            getValidNeighbors(current).forEach(n -> backtrack(targets, n));
        }
    }

    Set<Point2D> getValidNeighbors(Point2D p) {
        return Arrays.stream(MatrixDirections.MAIN_DIRECTIONS)
                .map(d -> p.add(d))
                .filter(pp -> isInside(pp))
                .filter(pp -> get(pp) == get(p) + 1)
                .collect(Collectors.toSet());
    }


}
