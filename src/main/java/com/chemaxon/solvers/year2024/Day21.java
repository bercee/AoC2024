package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.Point2D;
import org.jsoup.internal.StringUtil;

import java.util.*;

import static com.chemaxon.utils.MatrixDirections.*;

// NO DAMN REFACTOR FOR THIS DAY, THIS HAS BEEN INCREDIBLY NASTY.
// THIS WAS THE LAST CHALLENGE I NEEDED TO SOLVE THIS YEAR, I'M OFF TO ENJOYING WHAT'S LEFT OF A FAMILY CHRISTMAS

public class Day21 extends Solver {

    record PathEnds(String s1, String s2) {
    }

    private static final String NUMPAD_INPUT = "789\n456\n123\n 0A";
    private static final String ARROW_INPUT = " ^A\n<v>";
    private static final Map<Point2D, String> DIRECTIONS = new HashMap<>();

    static {
        DIRECTIONS.put(UP, "^");
        DIRECTIONS.put(DOWN, "v");
        DIRECTIONS.put(LEFT, "<");
        DIRECTIONS.put(RIGHT, ">");
    }

    List<String> targets;
    Map<PathEnds, List<List<String>>> shortestPaths = new HashMap<>();

    public Day21(List<String> input) {
        super(input);
        targets = new ArrayList<>(input);

        var numpadMatrix = Parsers.createStringArrays(Arrays.stream(NUMPAD_INPUT.split("\n")).toList());
        shortestPaths.putAll(findShortestPaths(numpadMatrix));
        var arrowMatrix = Parsers.createStringArrays(Arrays.stream(ARROW_INPUT.split("\n")).toList());
        shortestPaths.putAll(findShortestPaths(arrowMatrix));


    }

    Map<PathEnds, List<List<String>>> findShortestPaths(String[][] matrix) {
        Map<PathEnds, List<List<String>>> ret = new HashMap<>();
        var points = getPoints(matrix);
        for (String s1 : points.keySet()) {
            for (String s2 : points.keySet()) {
                PathEnds pe = new PathEnds(s1, s2);
                List<List<String>> dirs;
                if (s1.equals(s2)) {
                    dirs = List.of(List.of());
                } else {
                    var paths = getPathsBetween(points.get(s1), points.get(s2)).stream().filter(path -> verifyPath(points.get(s1), new HashSet<>(points.values()), path));
                    dirs = paths.map(path -> path.stream().map(DIRECTIONS::get).toList()).toList();
                }
                ret.put(pe, dirs);
            }
        }

        return ret;
    }

    private boolean verifyPath(Point2D start, Set<Point2D> valids, List<Point2D> path) {
        var current = start;
        if (!valids.contains(current)) {
            return false;
        }
        for (Point2D p : path) {
            current = current.add(p);
            if (!valids.contains(current)) {
                return false;
            }
        }

        return true;
    }

    private List<List<Point2D>> getPathsBetween(Point2D p1, Point2D p2) {

        var diff = p2.subtract(p1);
        var hDir = diff.j() > 0 ? RIGHT : LEFT;
        var vDir = diff.i() > 0 ? DOWN : UP;
        List<Point2D> path1 = new ArrayList<>();
        List<Point2D> path2 = new ArrayList<>();

        //horizontal first
        for (int i = 0; i < Math.abs(diff.j()); i++) {
            path1.add(hDir);
        }
        for (int i = 0; i < Math.abs(diff.i()); i++) {
            path1.add(vDir);
        }

        //vertical first
        for (int i = 0; i < Math.abs(diff.i()); i++) {
            path2.add(vDir);
        }
        for (int i = 0; i < Math.abs(diff.j()); i++) {
            path2.add(hDir);
        }


        return diff.j() == 0 || diff.i() == 0 ? List.of(path1) : List.of(path1, path2);
    }

    private Map<String, Point2D> getPoints(String[][] matrix) {
        Map<String, Point2D> points = new HashMap<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                var p = new Point2D(i, j);
                var s = matrix[i][j];
                if (s.equals(" ")) {
                    continue;
                }
                points.put(s, p);
            }
        }

        return points;
    }

    List<String> translateTarget(List<String> target) {
        List<String> ret = new ArrayList<>();

        for (int i = 0; i < target.size(); i++) {
            PathEnds pe;
            if (i == 0) {
                pe = new PathEnds("A", target.get(i));
            } else {
                pe = new PathEnds(target.get(i - 1), target.get(i));
            }
            try {
                List<List<String>> chooseFrom = new ArrayList<>(shortestPaths.get(pe));
                chooseFrom.sort((l1, l2) -> {
                    var f1 = l1.getFirst();
                    var f2 = l2.getFirst();
                    var pe1 = new PathEnds("A", f1);
                    var pe2 = new PathEnds("A", f2);
                    return Integer.compare(shortestPaths.get(pe1).getFirst().size(), shortestPaths.get(pe2).getFirst().size());
                });
                ret.addAll(chooseFrom.getLast());
            } catch (NullPointerException e) {
                LOGGER.error("{} {}", pe, shortestPaths.get(pe));
            }
            ret.add("A");
        }

        return ret;
    }

    List<String> translate3Times(List<String> target) {
        return translateTarget(translateTarget(translateTarget(target)));
    }


    @Override
    public String solvePart1() {
        var converts = targets.stream().map(s -> Arrays.stream(s.split("")).toList()).map(this::translate3Times).toList();
        int count = 0;
        for (int i = 0; i < targets.size(); i++) {
            int num = Integer.parseInt(targets.get(i).replace("A", ""));
            count += converts.get(i).size() * num;
        }

        //215374
        return String.valueOf(count);
    }

    long measureBag(Map<String, Long> bag) {
        long sum = 0;
        for (String s : bag.keySet()) {
            sum += bag.get(s) * (s.length() + 1);
        }
        return sum;
    }

    List<String> splitByAs(List<String> seq) {
        return Arrays.stream(StringUtil.join(seq, "").split("A")).toList();
    }

    @Override
    public String solvePart2() {
        long count = 0;
        for (String target : targets) {
            int num = Integer.parseInt(target.replace("A", ""));


            var start = Arrays.stream(target.split("")).toList();
            var current = translateTarget(start);
            Map<String, Long> bag = new LinkedHashMap<>();

            var splits = splitByAs(current);
            for (String s : splits) {
                bag.merge(s, 1L, Long::sum);
            }


            for (int i = 0; i < 25; i++) {
                Map<String, Long> nextBag = new LinkedHashMap<>();
                for (String segment : bag.keySet()) {
                    long occurrence = bag.get(segment);
                    if (segment.isEmpty()) {
                        nextBag.merge("", occurrence, Long::sum);
                    } else {

                        var converted = translateTarget(Arrays.stream((segment + "A").split("")).toList());
                        var convertedSplitByAs = splitByAs(converted);
                        convertedSplitByAs.forEach(s -> nextBag.merge(s, occurrence, Long::sum));
                    }
                }
                bag = nextBag;

            }

            count += num * measureBag(bag);
        }


        return String.valueOf(count);
    }
}
