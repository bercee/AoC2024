package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import com.chemaxon.utils.MatrixMap;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day25 extends Solver {
    List<MatrixMap<String>> matrixes = new ArrayList<>();
    List<MatrixMap<String>> topMatrixes = new ArrayList<>();
    List<MatrixMap<String>> bottomMatrixes = new ArrayList<>();


    public Day25(List<String> input) {
        super(input);
        var blocks = Arrays.stream(StringUtil.join(input, "\n").split("\n\n"));
        var arrays = blocks.map(block -> Arrays.stream(block.split("\n")).map(l -> l.split("")).toArray(String[][]::new)).toList();
        matrixes = arrays.stream().map(MatrixMap::new).toList();

        topMatrixes = matrixes.stream().filter(this::isTop).toList();
        bottomMatrixes = matrixes.stream().filter(m -> !this.isTop(m)).toList();

        topMatrixes.forEach(m -> LOGGER.info("{}", convert(m)));
        bottomMatrixes.forEach(m -> LOGGER.info("{}", convert(m)));

        LOGGER.info("total: {} top: {} bottom: {}", matrixes.size(), topMatrixes.size(), bottomMatrixes.size());

    }

    boolean isTop(MatrixMap<String> matrix) {
        var row1 = matrix.getRow(0);

        return row1.stream().allMatch("#"::equals);
    }

    List<Integer> convert(MatrixMap<String> matrix) {
        List<Integer> nums = new ArrayList<>();

        for (int i = 0; i < matrix.width; i++) {
            var col = matrix.getColumn(i);
            var num = (int) col.stream().filter("#"::equals).count() - 1;
            nums.add(num);
        }

        return nums;
    }

    boolean match(List<Integer> l1, List<Integer> l2) {
        int max = 5;
        var exp = l1.getFirst() + l2.getFirst();
        for (int i = 0; i < l1.size(); i++) {
            if (l1.get(i) + l2.get(i) > max) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String solvePart1() {
        int count = 0;
        for (MatrixMap<String> topMatrix : topMatrixes) {
            for (MatrixMap<String> bottomMatrix : bottomMatrixes) {
                LOGGER.info("{}", convert(topMatrix));
                LOGGER.info("{}", convert(bottomMatrix));
                LOGGER.info("{}", match(convert(topMatrix), convert(bottomMatrix)));
                System.out.println();
                if (match(convert(topMatrix), convert(bottomMatrix))) {
                    count++;
                }
            }
        }

        return String.valueOf(count);
    }

    @Override
    public String solvePart2() {
        return "";
    }
}
