package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day6 extends Solver {

    private final int width;
    private final int height;
    private final String[][] map;
    private final Point startP;
    private final Dir startD = new Dir(-1, 0);


    public Day6(List<String> input) {
        super(input);
        map = Parsers.createStringArrays(input);
        this.width = map[0].length;
        this.height = map.length;
        startP = getStartPoint();
        map[startP.i()][startP.j()] = ".";
    }

    private Point getStartPoint() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j].equals("^")) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }


    @Override
    public String solvePart1() {
        Simulation sim = new Simulation(startP, startD, map);
        sim.run();
        return convert(sim.visitedPoints.size());
    }


    @Override
    public String solvePart2() {
        Simulation sim = new Simulation(startP, startD, this.map);
        sim.run();

        var ret = sim.visitedPoints.stream().filter(p -> {
            if (p.equals(startP)) {
                return false;
            }
            var mapCopy = Arrays.stream(map).map(ArrayUtils::clone).toArray(String[][]::new);
            mapCopy[p.i()][p.j()] = "#";
            var subSim = new Simulation(startP, startD, mapCopy);
            return subSim.run() == Result.LOOP;
        }).count();

        return convert(ret);
    }

}


record Point(int i, int j) {
    Point move(Dir d) {
        return new Point(i + d.x(), j + d.y());
    }
}

record Dir(int x, int y) {
    Dir turn() {
        return new Dir(y, -x);
    }
}

record Positions(Point p, Dir dir) {
}

enum Result {
    LOOP, OUTSIDE
}

class Simulation {
    final String[][] map;
    final int h;
    final int w;

    final Point startPoint;
    final Dir startDir;


    Point currentPoint;
    Dir currentDir;

    Set<Positions> visitedPositions = new HashSet<>();
    Set<Point> visitedPoints = new HashSet<>();


    public Simulation(Point startPoint, Dir startDir, String[][] map) {
        this.startPoint = startPoint;
        this.startDir = startDir;
        this.map = map;
        this.h = map.length;
        this.w = map[0].length;

        this.currentPoint = startPoint;
        this.currentDir = startDir;
    }

    boolean isOutside(Point p) {
        return p.i() < 0 || p.i() >= h || p.j() < 0 || p.j() >= w;
    }

    boolean isFree(Point p) {
        return map[p.i()][p.j()].equals(".");
    }

    Result run() {
        visit();
        while (true) {
            var next = currentPoint.move(currentDir);

            if (isOutside(next)) {
                return Result.OUTSIDE;
            }

            while (!isFree(next)) {
                currentDir = currentDir.turn();
                next = currentPoint.move(currentDir);
            }
            currentPoint = next;


            if (visitedPositions.contains(new Positions(currentPoint, currentDir))) {
                return Result.LOOP;
            }

            visit();
        }
    }

    void visit() {
        visitedPoints.add(currentPoint);
        visitedPositions.add(new Positions(currentPoint, currentDir));
    }


}
