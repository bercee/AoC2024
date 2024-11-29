package com.chemaxon;

public class Main {

    private static final String ASSETS = "assets/";

    public static void main(String[] args) {
        new Connector("2022", "16").submit(1, "posdijf");
    }

    public static String getInputFile(String year, String day) {
        return ASSETS + "input_" + year + "_day_" + day;
    }
}