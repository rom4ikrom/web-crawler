package com.monzo.webcrawler.application;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class CrawlConfigParser {

    private static final String FILE_PATH = "/application/config/crawlConfig.json";

    @NonNull
    private final JsonMapper jsonMapper;

    public CrawlConfig crawlConfig() {
        try (InputStream inputStream = getClass().getResourceAsStream(FILE_PATH)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Crawl configuration is not found.");
            }
            return jsonMapper.readValue(inputStream, CrawlConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read crawl configuration.", e);
        }
    }

}
