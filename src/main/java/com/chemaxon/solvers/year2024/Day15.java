package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixDirections;
import com.chemaxon.utils.MatrixMap;
import com.chemaxon.utils.Point2D;

import java.util.*;
import java.util.stream.Collectors;

//THIS IS VERY UGLY BUT I HAVE NO TIME TO CLEAN THIS UP


public class Day15 extends Solver {

    static Map<String, Point2D> DIRS = new HashMap<>();

    static {
        DIRS.put("^", MatrixDirections.UP);
        DIRS.put("v", MatrixDirections.DOWN);
        DIRS.put("<", MatrixDirections.LEFT);
        DIRS.put(">", MatrixDirections.RIGHT);
    }

    static class Game {
        MatrixMapExt map;
        Point2D fish;
        Set<Point2D> boxes;
        Set<Point2D> boxes2 = new HashSet<>();

        public Game(MatrixMapExt map, Point2D fish, Set<Point2D> boxes) {
            this.map = map;
            this.fish = fish;
            this.boxes = boxes;
        }

        void extend() {
            fish = new Point2D(fish.i(), fish.j() * 2);
            boxes = boxes.stream().map(b -> new Point2D(b.i(), b.j() * 2)).collect(Collectors.toSet());
            boxes2 = boxes.stream().map(b -> new Point2D(b.i(), b.j() + 1)).collect(Collectors.toSet());

            String[][] matrix2 = new String[map.height][map.width * 2];
            for (int i = 0; i < map.height; i++) {
                for (int j = 0; j < map.width; j++) {
                    if (map.isFree(new Point2D(i, j))) {
                        matrix2[i][j * 2] = ".";
                        matrix2[i][j * 2 + 1] = ".";
                    } else {
                        matrix2[i][j * 2] = "#";
                        matrix2[i][j * 2 + 1] = "#";
                    }
                }
            }
            map = new MatrixMapExt(matrix2);
        }

        int GPS() {
            return boxes.stream().mapToInt(b -> b.i() * 100 + b.j()).sum();
        }

        void move(Point2D dir) {
            if (!boxes2.isEmpty()) {
                move2(dir);
                return;
            }

            List<Point2D> movables = new ArrayList<>();
            Point2D current = null;

            while (true) {
                if (current == null) {
                    current = fish.clone();
                } else {
                    current = current.add(dir);
                }

                var next = current.add(dir);
                if (map.isFree(next)) {
                    movables.add(current);
                    if (!boxes.contains(next)) {
                        break;
                    }
                } else {
                    movables.clear();
                    break;
                }
            }

            if (!movables.isEmpty()) {
                movables.remove(fish);
                fish = fish.add(dir);

                movables.reversed().forEach(b -> {
                    boxes.remove(b);
                    boxes.add(b.add(dir));
                });
            }

        }

        private void move2(Point2D dir) {
            if (dir.equals(MatrixDirections.LEFT) || dir.equals(MatrixDirections.RIGHT)) {
                moveHorizontally(dir);
            } else {
                moveVertically(dir);
            }
        }

        private void moveHorizontally(Point2D dir) {
            List<Point2D> movables = new ArrayList<>();
            Point2D current = null;

            while (true) {
                if (current == null) {
                    current = fish.clone();
                } else {
                    current = current.add(dir);
                }


                var next = current.add(dir);
                if (map.isFree(next)) {
                    if (boxes.contains(next) || boxes2.contains(next)) {
                        movables.add(current);
                        movables.add(current.add(dir));
                        current = current.add(dir);
                    } else {
                        movables.add(current);
                        break;
                    }

                } else {
                    movables.clear();
                    break;
                }
            }


            if (!movables.isEmpty()) {
                movables.remove(fish);
                fish = fish.add(dir);

                movables.reversed().forEach(b -> {
                    if (boxes.contains(b)) {
                        boxes.remove(b);
                        boxes.add(b.add(dir));
                    }
                    if (boxes2.contains(b)) {
                        boxes2.remove(b);
                        boxes2.add(b.add(dir));
                    }
                });
            }
        }

        private void moveVertically(Point2D dir) {
            List<List<Point2D>> movables = new ArrayList<>();
            movables.add(List.of(fish));

            while (true) {
                List<Point2D> nexts = movables.getLast().stream().map(p -> p.add(dir)).toList();
                if (nexts.stream().anyMatch(p -> !map.isFree(p))) {
                    movables.clear();
                    break;
                }

                var touches1 = new ArrayList<>(nexts.stream().filter(p -> boxes.contains(p)).toList());
                var touches2 = new ArrayList<>(nexts.stream().filter(p -> boxes2.contains(p)).toList());

                touches1.addAll(touches2.stream().map(p -> p.add(MatrixDirections.LEFT)).toList());
                touches2.addAll(touches1.stream().map(p -> p.add(MatrixDirections.RIGHT)).toList());

                if (touches1.isEmpty() && touches2.isEmpty()) {
                    break;
                }

                var nextMovables = new ArrayList<Point2D>();
                nextMovables.addAll(touches1);
                nextMovables.addAll(touches2);

                movables.add(nextMovables);
            }

            if (!movables.isEmpty()) {
                movables.remove(movables.getFirst());
                fish = fish.add(dir);

                movables.reversed().forEach(row -> {
                            row.forEach(b -> {
                                if (boxes.contains(b)) {
                                    boxes.remove(b);
                                    boxes.add(b.add(dir));
                                }
                                if (boxes2.contains(b)) {
                                    boxes2.remove(b);
                                    boxes2.add(b.add(dir));
                                }
                            });
                        }
                );
            }


        }

        void print() {
            for (int i = 0; i < map.height; i++) {
                for (int j = 0; j < map.width; j++) {
                    var p = new Point2D(i, j);
                    if (!map.isFree(p)) {
                        System.out.print("#");
                    } else {
                        if (fish.equals(p)) {
                            System.out.print("@");
                        } else if (boxes.contains(p)) {
                            if (boxes2.isEmpty()) {
                                System.out.print("O");
                            } else {
                                System.out.print("[");
                            }
                        } else if (boxes2.contains(p)) {
                            System.out.print("]");
                        } else {
                            System.out.print(".");
                        }
                    }
                }
                System.out.println();
            }
        }
    }

    static class MatrixMapExt extends MatrixMap<String> {
        public MatrixMapExt(String[][] matrix) {
            super(matrix);
        }

        boolean isFree(Point2D p) {
            return isInside(p) && !get(p).equals("#");
        }
    }

    List<Point2D> directions;
    Game game;


    public Day15(List<String> input) {
        super(input);

        var emptyIdx = input.indexOf("");
        var mapInput = input.subList(0, emptyIdx);
        var matrix = Parsers.createStringArrays(mapInput);
        Point2D fish = null;
        Set<Point2D> boxes = new HashSet<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j].equals("@")) {
                    fish = new Point2D(i, j);
                    matrix[i][j] = ".";
                } else if (matrix[i][j].equals("O")) {
                    boxes.add(new Point2D(i, j));
                    matrix[i][j] = ".";
                }
            }
        }

        game = new Game(new MatrixMapExt(matrix), fish, boxes);
        var directionsInput = input.subList(emptyIdx + 1, input.size());
        directions = Arrays.stream(String.join("", directionsInput).split("")).map(s -> DIRS.get(s)).toList();
    }

    @Override
    public String solvePart1() {
        directions.forEach(game::move);
        return convert(game.GPS());
    }

    @Override
    public String solvePart2() {
        game.extend();
        directions.forEach(game::move);
        game.print();
        return convert(game.GPS());
    }
}
