package com.chemaxon;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.List;

public class Parsers {

    public static RealMatrix createMatrix(List<String> lines) {
        return createMatrix(lines, "\\s+");
    }

    public static RealMatrix createMatrix(List<String> lines, String regexPattern) {
        var data = lines.stream().map(l -> Arrays.stream(l.split(regexPattern)).mapToDouble(Double::parseDouble).toArray()).toArray(double[][]::new);
        return MatrixUtils.createRealMatrix(data);
    }

    public static String createOneLine(List<String> lines) {
        return String.join("", lines);
    }

    public static String[][] createStringArrays(List<String> lines) {
        return createStringArrays(lines, "");
    }

    public static String[][] createStringArrays(List<String> lines, String regexPattern) {
        return lines.stream().map(l -> l.split(regexPattern)).toArray(String[][]::new);
    }
}
