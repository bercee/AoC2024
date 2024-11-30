package com.chemaxon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Solver {
    private final List<String> input;

    public Solver(List<String> input) {
        this.input = input;
    }

    protected List<String> getInput() {
        return input;
    }

    public abstract String solvePart1();

    public abstract String solvePart2();
}
