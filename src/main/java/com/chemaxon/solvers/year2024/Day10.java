package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixDirections;
import com.chemaxon.utils.MatrixMap;
import com.chemaxon.utils.Point2D;

import java.util.*;
import java.util.stream.Collectors;

public class Day10 extends Solver {

    MatrixMap<Integer> map;
    Set<Point2D> startPoints;


    public Day10(List<String> input) {
        super(input);
        map = new MatrixMap<>(Parsers.createIntegerArrays(input));
        startPoints = map.filter(p -> p == 0);
    }

    @Override
    public String solvePart1() {
        int count = 0;
        for (Point2D startPoint : startPoints) {
            Set<List<Point2D>> targets = new HashSet<>();
            List<Point2D> firstPath = List.of(startPoint);
            backtrack(targets, firstPath);
            Set<Point2D> ends = new HashSet<>();
            targets.forEach(p -> ends.add(p.getLast()));
            count += ends.size();
        }
        return convert(count);
    }

    @Override
    public String solvePart2() {
        int count = 0;
        for (Point2D startPoint : startPoints) {
            Set<List<Point2D>> targets = new HashSet<>();
            List<Point2D> firstPath = List.of(startPoint);
            backtrack(targets, firstPath);
            count += targets.size();
        }
        return convert(count);
    }

    void backtrack(Set<List<Point2D>> paths, List<Point2D> currentPath) {
        var current = currentPath.getLast();
        if (map.get(current) == 9) {
            paths.add(new ArrayList<>(currentPath));
        } else {
            var neighbors = getValidNeighbors(current).stream().toList();
            for (Point2D neighbor : neighbors) {
                var nextPath = new ArrayList<>(currentPath);
                nextPath.add(neighbor);
                backtrack(paths, nextPath);
            }
        }
    }

    Set<Point2D> getValidNeighbors(Point2D p) {
        return Arrays.stream(MatrixDirections.MAIN_DIRECTIONS)
                .map(p::add)
                .filter(map::isInside)
                .filter(pp -> map.get(pp) == map.get(p) + 1)
                .collect(Collectors.toSet());
    }


}
