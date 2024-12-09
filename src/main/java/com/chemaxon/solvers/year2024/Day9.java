package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//THIS IS VERY UGLY BUT I HAVE NO TIME TO CLEAN THIS UP

public class Day9 extends Solver {

    record Block(int length, int id) {

    }

    ArrayList<Integer> line = new ArrayList<>();
    ArrayList<Block> blocks = new ArrayList<>();

    public Day9(List<String> input) {
        super(input);

        int id = 0;
        for (int i = 0; i < input.getFirst().length(); i++) {
            int k = Character.getNumericValue(input.get(0).charAt(i));
            if (i % 2 == 0) {
                blocks.add(new Block(k, id));
                for (int j = 0; j < k; j++) {
                    line.add(id);
                }
                id++;
            } else {
                blocks.add(new Block(k, -1));
                for (int j = 0; j < k; j++) {
                    line.add(null);
                }
            }
        }

//        System.out.println(line);

    }

    @Override
    public String solvePart1() {
        while (true) {
            var firstNullIdx = line.indexOf(null);
            var lastNotNullIdx = findLastNotNullIdx();
            if (firstNullIdx < lastNotNullIdx) {

                Collections.swap(line, firstNullIdx, lastNotNullIdx);
            } else {
                break;
            }

        }
        System.out.println(line);
        return convert(checksum());
    }

    private int findLastNotNullIdx() {
        for (int i = line.size() - 1; i >= 0; i--) {
            if (line.get(i) != null) {
                return i;
            }
        }
        return -1;
    }

    private long checksum() {
        long sum = 0;
        for (int i = 0; i < line.size(); i++) {
            sum += line.get(i) != null ? line.get(i) * i : 0;
        }
        return sum;
    }

    private int lengthOfBlockStartingWith(int idx, int direction) {
        var id = line.get(idx);
        int count = 0;
        int nextIdx;
        Integer nextId;
        boolean cont = true;
        do {
            count++;
            nextIdx = idx + direction * count;
            if (!isInLine(nextIdx)) {
                break;
            }
            nextId = line.get(nextIdx);
            cont = isInLine(nextIdx) && Objects.equals(nextId, id);
        } while (cont);

        return count;
    }

    private boolean isInLine(int idx) {
        return idx >= 0 && idx < line.size();
    }

    private int findNextEmptyBlockStrtIdx(int idxFrom) {
        var ret = idxFrom;
        while (isInLine(ret) && line.get(ret) != null) {
            ret++;
        }
        return ret;
    }

    private int getLastFileBlockStartIdx() {
        var id = IterableUtils.find(line.reversed(), Objects::nonNull);
        var idx = line.size() - line.reversed().indexOf(id);
        return idx;
    }

    @Override
    public String solvePart2() {
        LOGGER.info("solvePart2");
        var files = blocks.stream().filter(b -> b.id > -1).toList().reversed();
        for (Block f : files) {
            LOGGER.info("processing {}", f.id);
//            System.out.println(f);
            for (int i = 0; i < blocks.size(); i++) {
                var b = blocks.get(i);
                if (b.id == -1 && b.length >= f.length && blocks.indexOf(b) < blocks.indexOf(f)) {
                    LOGGER.info("moving {}", f.id);
                    blocks.set(blocks.indexOf(f), new Block(f.length, -1));
                    blocks.set(i, f);
                    if (b.length > f.length) {
                        var remainder = new Block(b.length - f.length, -1);
                        blocks.add(i + 1, remainder);
                    }
                    break;
                }
            }
        }
        LOGGER.info("part2 solved");

//        print();


        return convert(checksum2());
    }

    long checksum2() {
        LOGGER.info("calculating checksum");
        var line = toList();
        long sum = 0;
        for (int i = 0; i < line.size(); i++) {
            sum += line.get(i) != null ? line.get(i) * i : 0;
        }
        return sum;
    }

    void print() {
        for (Block block : blocks) {
            for (int i = 0; i < block.length; i++) {
                System.out.print(block.id == -1 ? "." : block.id);
            }
        }
        System.out.println();
    }

    List<Integer> toList() {
        LOGGER.info("converting to list...");
        var list = new ArrayList<Integer>();
        for (Block block : blocks) {
            for (int i = 0; i < block.length; i++) {
                list.add(block.id == -1 ? null : block.id);
            }
        }
        return list;
    }

}
