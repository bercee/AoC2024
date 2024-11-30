package com.chemaxon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;

public class Main {
    public static final String YEAR = System.getProperty("year");
    public static final String DAY = System.getProperty("day");
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        var connector = new Connector(YEAR, DAY);
        if (args.length > 0) {
            switch (args[0]) {
                case "testPart1":
                    LOGGER.info("Testing part 1");
                    test(connector.getTestResult(1), createSolver(connector.getTestInput()).solvePart1());
                    break;
                case "solvePart1":
                    LOGGER.info("Solving part 1: {}", createSolver(connector.getInput()).solvePart1());
                    break;
                case "solveAndSubmitPart1":
                    var answer1 = createSolver(connector.getInput()).solvePart1();
                    LOGGER.info("Solving and submit part 1: {}", answer1);
                    connector.submit(1, answer1);
                    break;
                case "testPart2":
                    LOGGER.info("Testing part 2");
                    test(connector.getTestResult(2), createSolver(connector.getTestInput()).solvePart2());
                    break;
                case "solvePart2":
                    LOGGER.info("Solving part 2: {}", createSolver(connector.getInput()).solvePart2());
                    break;
                case "solveAndSubmitPart2":
                    var answer2 = createSolver(connector.getInput()).solvePart2();
                    LOGGER.info("Solving and submit part 2: {}", answer2);
                    connector.submit(2, answer2);
                    break;
                default:
                    LOGGER.error("Unknown command: {}", args[0]);
                    break;
            }
        } else {
            LOGGER.info("No input provided.");
        }
    }


    private static void test(String expected, String actual) {
        if (expected.equals(actual)) {
            LOGGER.info("TEST PASSED. {}", actual);
        } else {
            LOGGER.error("TEST FAILED. Expected: {} Actual: {}", expected, actual);
        }
    }


    private static Solver createSolver(List<String> input) {
        var solverName = "com.chemaxon.solvers.year" + YEAR + ".Day" + DAY;
        try {
            Class<?> clazz = Class.forName(solverName);
            Class<?>[] paramTypes = { List.class };
            Constructor<?> constructor = clazz.getConstructor(paramTypes);
            return (Solver) constructor.newInstance(input);
        } catch (Exception e) {
            LOGGER.error("Issue in creating solver {}", solverName, e);
            throw new RuntimeException(e);
        }
    }


}