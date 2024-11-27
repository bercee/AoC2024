package com.chemaxon;

public class Main {

    private static final String ASSETS = "assets/";

    public static void main(String[] args) {
        new Downloader("2022", "3").checkAndDownload();
    }

    public static String getInputFile(String year, String day) {
        return ASSETS + "input_" + year + "_day_" + day;
    }
}