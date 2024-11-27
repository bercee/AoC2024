package com.chemaxon;

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

public class Downloader {
    private final String year;
    private final String day;

    private static final Logger LOGGER = LoggerFactory.getLogger(Downloader.class);


    public Downloader(String year, String day) {
        this.year = year;
        this.day = day;
    }

    public void checkAndDownload() {
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

    private String downloadInput() {
        LOGGER.info("Downloading input for year {} day {}", year, day);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(getURL()))
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

    private String getURL() {
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
