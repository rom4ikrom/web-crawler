package com.monzo.webcrawler.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CrawlConfig {

    String startUrl;
    int numberOfThreads;
    int numberOfLinksToDiscover;

    @JsonCreator
    CrawlConfig(
            @JsonProperty("startUrl") String startUrl,
            @JsonProperty("numberOfThreads") int numberOfThreads,
            @JsonProperty("numberOfLinksToDiscover") int numberOfLinksToDiscover) {
        if (isBlank(startUrl)) {
            throw new IllegalArgumentException("Start url must be provided.");
        }
        if (!withinLimitForNumberOfThreads(numberOfThreads)) {
            throw new IllegalArgumentException("Number of threads must between 1 and 30.");
        }
        if (!withinLimitForNumberOfLinksDiscovered(numberOfLinksToDiscover)) {
            throw new IllegalArgumentException("Number of links to discover must be greater than 0.");
        }
        this.startUrl = startUrl;
        this.numberOfThreads = numberOfThreads;
        this.numberOfLinksToDiscover = numberOfLinksToDiscover;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean withinLimitForNumberOfThreads(int value) {
        return value > 0 && value <= 30;
    }

    private boolean withinLimitForNumberOfLinksDiscovered(int value) {
        return value > 0;
    }

}
