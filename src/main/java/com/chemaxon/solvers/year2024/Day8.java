package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.Point2D;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.*;

public class Day8 extends Solver {

    String[][] map;
    int h;
    int w;

    ArrayListValuedHashMap<String, Point2D> antennas = new ArrayListValuedHashMap<>();

    public Day8(List<String> input) {
        super(input);
        map = Parsers.createStringArrays(input);
        h = map.length;
        w = map[0].length;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                var p = new Point2D(i, j);
                if (!value(p).equals(".")) {
                    antennas.put(value(p), p);
                }
            }
        }

        System.out.println(antennas);
    }

    String value(Point2D point) {
        if (!isOutside(point)) {
            return map[point.i()][point.j()];
        }
        return null;
    }

    boolean isOutside(Point2D p) {
        return p.i() < 0 || p.i() >= h || p.j() < 0 || p.j() >= w;
    }

    boolean isFree(Point2D p) {
        return map[p.i()][p.j()].equals(".");
    }

    @Override
    public String solvePart1() {
        return convert(antennas.keySet().stream().mapToInt(s -> getAntinodes(s, false).size()).sum());
    }


    @Override
    public String solvePart2() {
        var interfs =  antennas.keySet().stream().map(s -> getAntinodes(s, true)).reduce(new HashSet<Point2D>(), (a, b) -> {a.addAll(b); return a;});
        var antennaNum = antennas.keySet().stream().mapToInt(s -> antennas.get(s).size()).sum();
        return convert(interfs.size() + antennaNum);
    }


    private Set<Point2D> getAntinodes(String value, boolean cont) {
        var ret = new HashSet<Point2D>();

        var nodes = antennas.get(value);
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) {
                    continue;
                }
                var p1 = nodes.get(i);
                var p2 = nodes.get(j);
                var diff = p1.subtract(p2);
                var ans1 = getInterferences(p1, diff, cont);
                ret.addAll(ans1);
            }
        }

        return ret;
    }

    private Set<Point2D> getInterferences(Point2D start, Point2D  diff, boolean cont) {
        var ret = new HashSet<Point2D>();
        var p = start.add(diff);
        while (!isOutside(p)) {
            if (isFree(p)) {
                ret.add(p);
            }
            if (!cont) {
                break;
            }
            p = p.add(diff);
        }
        return ret;
    }





}
