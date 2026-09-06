package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrawlTrackerTest {

    private CrawlTracker underTest;

    @BeforeEach
    void setup() {
        underTest = new CrawlTracker("something.com", 2);
    }

    @Test
    void acceptsUrlFromSameDomain() {
        NormalisedUrl url = new NormalisedUrl("https://something.com/page", "something.com");

        assertThat(underTest.submit(url)).isTrue();
    }

    @Test
    void rejectsUrlFromDifferentDomain() {
        NormalisedUrl url = new NormalisedUrl("https://other.com/page", "other.com");

        assertThat(underTest.submit(url)).isFalse();
    }

    @Test
    void rejectsDuplicateUrl() {
        NormalisedUrl url = new NormalisedUrl("https://something.com/page", "something.com");

        assertThat(underTest.submit(url)).isTrue();
        assertThat(underTest.submit(url)).isFalse();
    }

    @Test
    void rejectsOverLimit() {
        NormalisedUrl firstUrl = new NormalisedUrl("https://something.com/page/1", "something.com");
        NormalisedUrl secondUrl = new NormalisedUrl("https://something.com/page/2", "something.com");
        NormalisedUrl thirdUrl = new NormalisedUrl("https://something.com/page/3", "something.com");

        assertThat(underTest.submit(firstUrl)).isTrue();
        assertThat(underTest.submit(secondUrl)).isTrue();
        assertThat(underTest.submit(thirdUrl)).isFalse();
    }

}