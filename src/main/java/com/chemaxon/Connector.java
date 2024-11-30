package com.chemaxon;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Connector {
    private final String year;
    private final String day;

    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);
    private static final String ASSETS = "assets/";

    private interface ContentProvider {
        String getContent();
    }

    public Connector(String year, String day) {
        this.year = year;
        this.day = day;
    }

    public List<String> getInput() {
        downloadInputIfMissing();
        try {
            return Files.readAllLines(new File(getInputFile()).toPath());
        } catch (IOException e) {
            LOGGER.error("Could not read input file.", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getTestInput() {
        downloadTestInputIfMissing();
        try {
            return Files.readAllLines(new File(getTestInputFile()).toPath());
        } catch (IOException e) {
            LOGGER.error("Could not read test input file.", e);
            throw new RuntimeException(e);
        }
    }

    public String getTestResult(int part) {
        downloadTestResultIfMissing(part);
        try {
            var lines = Files.readAllLines(new File(getTestResultFile()).toPath());
            var pattern = "part" + part + "=";
            var line = lines.stream().filter(l -> l.startsWith(pattern)).findFirst().orElseThrow();
            return line.substring(pattern.length());
        } catch (IOException | NoSuchElementException e ) {
            LOGGER.error("Could not read test result for part {}", part);
            throw new RuntimeException(e);
        }

    }

    private void downloadInputIfMissing() {
        checkAndWrite(getInputFile(), () -> download(getInputURL()));
    }

    private void downloadTestInputIfMissing() {
        checkAndWrite(getTestInputFile(), () -> extractTestInput(download(getTaskURL())));
    }

    private void downloadTestResultIfMissing(int part) {
        try {
            var file = new File(getTestResultFile());
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("Could not create file");
                }
            }
            var lines = Files.readAllLines(file.toPath());
            if (lines.stream().noneMatch(l -> l.startsWith("part" + part + "="))) {
                var data = extractTestResult(download(getTaskURL()), part);
                var line = "part" + part + "=" + data;
                Files.write(file.toPath(), Collections.singletonList(line), StandardOpenOption.APPEND);
            } else {
                LOGGER.info("Test result already exists for part {}", part);
            }
        } catch (IOException e) {
            LOGGER.error("Error getting test result.", e);
        }
    }

    private void checkAndWrite(String filename, ContentProvider provider) {
        var file = new File(filename);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(provider.getContent());
                    }
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                LOGGER.error("Error creating file {}", filename);
                LOGGER.error("Error creating file", e);
            }
        } else {
            LOGGER.info("File exists {}", filename);
        }
    }

    private String extractTestResult(String html, int part) {
        var pattern1 = Pattern.compile("[Ii]n this example[\\s\\S]*?<em><code>([\\s\\S]*?)</code></em>", Pattern.DOTALL);
        var pattern2 = Pattern.compile("[Ii]n this example[\\s\\S]*?<code><em>([\\s\\S]*?)</em></code>", Pattern.DOTALL);

        String result = findMatch(pattern1, html, part);
        if (result == null) {
            result = findMatch(pattern2, html, part);
        }

        if (result == null) {
            LOGGER.warn("Could not find test solution for part {}", part);
            return getUserInput("Please type the correct test result manually:\n");
        }

        LOGGER.info("Test solution for part {} found: {}", part, result);
        return result;
    }

    private static String findMatch(Pattern pattern, String rawData, int part) {
        var matcher = pattern.matcher(rawData);
        String match = null;
        for (int i = 0; i < part && matcher.find(); i++) {
            match = matcher.group(1);
        }
        return match;
    }

    private static String getUserInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String extractTestInput(String html) {
        return extractTestInput(html, 0);
    }

    private String extractTestInput(String html, int part) {
        var pattern = Pattern.compile("<article class=\"day-desc\">[\\s\\S]*?</article>");
        var matcher = pattern.matcher(html);
        if (matcher.find()) {
            var taskDescription = matcher.group(part);
            var pattern2 = Pattern.compile("<pre><code>([\\s\\S]*?)</code></pre>");
            var matcher2 = pattern2.matcher(taskDescription);
            if (matcher2.find()) {
                var ret = matcher2.group(1);
                LOGGER.info("Test input found: \n{}", ret);
                return ret;
            }
        }
        LOGGER.info("Test input not found");
        return "";
    }

    public void submit(int part, String answer) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            LOGGER.info(getAnswerURL());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getAnswerURL()))
                    .header("Accept-language", "en-US,en;q=0.9")
                    .header("Cookie", "session=" + getSessionID())
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("level=" + part + "&answer=" + answer))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Answer to solution: {}", response.statusCode());
            var doc = Jsoup.parse(response.body());
            var message = doc.select("body > main > article").text();
            LOGGER.info("Answer to solution: {}", message);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAnswerURL() {
        return "https://adventofcode.com/" + year + "/day/" + day + "/answer";
    }

    private String getTaskURL() {
        return "https://adventofcode.com/" + year + "/day/" + day;
    }

    private String getInputURL() {
        return "https://adventofcode.com/" + year + "/day/" + day + "/input";
    }


    private String download(String url) {
        LOGGER.info("Downloading url {}", url);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Accept-language", "en-US,en;q=0.9")
                    .header("Cookie", "session=" + getSessionID())
                    .GET()
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var status = response.statusCode();
            LOGGER.info("Download result: {}", response.statusCode());

            if (!String.valueOf(status).startsWith("2")) {
                LOGGER.error("Error");
                LOGGER.error(response.body());
                return "";
            }

            return response.body();


        } catch (URISyntaxException | IOException | InterruptedException e) {
            LOGGER.error("Error downloading", e);
            return "";
        }
    }


    private String getSessionID() {
        try {
            return Files.readString(new File("./session_id.txt").toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getInputFile() {
        return getFileBase(year, day) + "_input.txt";
    }

    private String getTestInputFile() {
        return getFileBase(year, day) + "_test_input.txt";
    }

    private String getTestResultFile() {
        return getFileBase(year, day) + "_test_result.txt";
    }

    private static String getFileBase(String year, String day) {
        return ASSETS + "year_" + year + "_day_" + day;
    }
}
