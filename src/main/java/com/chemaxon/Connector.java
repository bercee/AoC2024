package com.chemaxon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

public class Connector {
    private final String year;
    private final String day;

    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);


    public Connector(String year, String day) {
        this.year = year;
        this.day = day;
    }

    public void downloadInputIfMissing() {
        File file = new File(Main.getInputFile(year, day));
        try {
            if (!file.exists() && file.createNewFile()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(downloadInput());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error creating file", e);
        }
    }

    public void submit(int part, String answer) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            LOGGER.info(getAnswerURL());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getAnswerURL()))
                    .header("Accept-language", "en-US,en;q=0.9")
                    .header("Cookie", "session=" + getSessionID())
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("level="+part+"&answer="+answer))
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


    private String downloadInput() {
        LOGGER.info("Downloading input for year {} day {}", year, day);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(getInputURL()))
                    .header("Accept-language", "en-US,en;q=0.9")
                    .header("Cookie", "session=" + getSessionID())
                    .GET()
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var status = response.statusCode();
            LOGGER.info("Download result: {}", response.statusCode());

            if (!String.valueOf(status).startsWith("2")) {
                LOGGER.error("Error");
                System.err.println(response.body());
                throw new RuntimeException("Error in downloading");
            }

            return response.body();


        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getInputURL() {
        return "https://adventofcode.com/" + year + "/day/" + day + "/input";
    }

    private String getSessionID() {
        try {
            return Files.readString(new File("./session_id.txt").toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
