package com.chemaxon.solvers.year2024;

import com.chemaxon.Parsers;
import com.chemaxon.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 extends Solver {
    private final String line;

    public Day3(List<String> input) {
        super(input);
        line = Parsers.createOneLine(input);
    }

    @Override
    public String solvePart1() {
        return convert(getMuls(line).stream().mapToInt(this::calcMul).sum());
    }

    @Override
    public String solvePart2() {
        var muls = getMulsWithInstructions(line);
        boolean enabled = true;
        int count = 0;
        for (String mul : muls) {
            if (mul.equals("do()")) {
                enabled = true;
            } else if (mul.equals("don't()")) {
                enabled = false;
            } else {
                if (enabled) {
                    count += calcMul(mul);
                }
            }
        }
        return convert(count);
    }

    private List<String> getMuls(String line) {
        var ret = new ArrayList<String>();
        Pattern p = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)");
        Matcher m = p.matcher(line);
        while (m.find()) {
            ret.add(m.group());
        }
        return ret;
    }

    private List<String> getMulsWithInstructions(String line) {

        var ret = new ArrayList<String>();
        Pattern p = Pattern.compile("(mul\\(\\d{1,3},\\d{1,3}\\)|do\\(\\)|don't\\(\\))");
        Matcher m = p.matcher(line);
        while (m.find()) {
            ret.add(m.group());
        }
        return ret;
    }

    private int calcMul(String mul) {
        var nums = mul.replace("mul", "").replace("(", "").replace(")", "");
        var data = nums.split(",");
        return Integer.parseInt(data[0]) * Integer.parseInt(data[1]);
    }
}
