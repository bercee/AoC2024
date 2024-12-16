package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixDirections;
import com.chemaxon.utils.Point2D;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 extends Solver {
    record Robot(Point2D p0, Point2D v) {
    }

    record Quadrant(int i1, int i2, int i3, int i4) {
    }

    List<Robot> robots;

    static String PATTERN = "p=(\\d+),(\\d+) v=([-]?\\d+),([-]?\\d+)";

//    int w = 11;
//    int h = 7;

    int w = 101;
    int h = 103;

    int t = 100;


    public Day14(List<String> input) {
        super(input);
        robots = input.stream().map(s -> {
            Matcher m = Pattern.compile(PATTERN).matcher(s);
            m.find();
            return new Robot(
                    new Point2D(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                    new Point2D(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
        }).toList();
    }

    void print(List<Point2D> points) {
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                var p = new Point2D(j, i);
                int c = (int) points.stream().filter(pp -> pp.equals(p)).count();
                System.out.print(c > 0 ? c + "" : ".");
            }
            System.out.println();
        }
    }

    Point2D map(Point2D p) {
        return new Point2D((p.i() % w + w) % w, (p.j() % h + h) % h);
    }

    Point2D move(Robot robot, int by) {
        return map(robot.p0.add(robot.v.multiply(by)));
    }

    Quadrant getQuadrant(List<Point2D> points) {
        int c0 = 0;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;

        int halfW = (w - 1) / 2;
        int halfH = (h - 1) / 2;

        for (Point2D p : points) {
            if (p.i() < halfW) {
                if (p.j() < halfH) {
                    c0++;
                } else if (p.j() > halfH) {
                    c1++;
                }
            } else if (p.i() > halfW) {
                if (p.j() < halfH) {
                    c2++;
                } else if (p.j() > halfH) {
                    c3++;
                }
            }
        }

        return new Quadrant(c0, c1, c2, c3);
    }

    int countQuadrant(List<Point2D> points) {
        var quadrant = getQuadrant(points);

        return quadrant.i1 * quadrant.i2 * quadrant.i3 * quadrant.i4;
    }

    boolean isSymmetrical(List<Point2D> points) {
        final int halfW = (w - 1) / 2;
        return points.stream().allMatch(p -> {
            var pp = new Point2D((p.i() - halfW) * -1 + halfW, p.j());
            return points.contains(pp);
        });
    }

    boolean isChristmasTree(List<Point2D> points) {
        return points.stream()
                .filter(p -> Arrays.stream(MatrixDirections.ALL_DIRECTIONS)
                        .map(d -> d.add(p)).anyMatch(points::contains)).count() > points.size() * 0.6;
    }


    @Override
    public String solvePart1() {
        var points = robots.stream().map(robot -> move(robot, t)).toList();
        print(points);
        return convert(countQuadrant(points));
    }

    @Override
    public String solvePart2() {
        int i = 0;
        while (true) {
            i++;
            final int ii = i;
            var points = robots.stream().map(robot -> move(robot, ii)).toList();
            if (isChristmasTree(points)) {
                print(points);
                break;
            }

        }
        return convert(i);
    }
}
