package com.chemaxon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Solver {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Solver.class);

    private final List<String> input;

    public Solver(List<String> input) {
        this.input = input;
    }

    protected List<String> getInput() {
        return input;
    }

    public abstract String solvePart1();

    public abstract String solvePart2();


    protected String convert(int answer) {
        return "" + answer;
    }

    protected String convert(double answer) {
        return "" + answer;
    }

    protected String convert(long answer) {
        return "" + answer;
    }

    protected String convertAsInt(double answer) {
        return "" + (int) answer;
    }
}
