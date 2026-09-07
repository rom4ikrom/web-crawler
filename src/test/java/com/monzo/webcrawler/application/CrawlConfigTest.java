package com.monzo.webcrawler.application;

import com.monzo.webcrawler.TestResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.*;

class CrawlConfigTest {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @ParameterizedTest
    @CsvSource(value = {
            ",                      1,  1,    Start url must be provided.",
            "'',                    1,  1,    Start url must be provided.",
            "' ',                   1,  1,    Start url must be provided.",
            "http://something.com,  0,  1,    Number of threads must between 1 and 30.",
            "http://something.com,  0,  31,   Number of threads must between 1 and 30.",
            "http://something.com,  1,  0,    Number of links to discover must be greater than 0."
    })
    void throwsExceptionIfConfigIsInvalid(
            String startUrl,
            int numberOfThreads,
            int numberOfUrlsToCrawl,
            String expectedMessage) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CrawlConfig(startUrl, numberOfThreads, numberOfUrlsToCrawl))
                .withMessage(expectedMessage);
    }

    @Test
    void createsValidConfig() {
        // given
        String configAsString = TestResources.read("/application/config/testCrawlConfig.json");

        // when
        CrawlConfig crawlConfig = JSON_MAPPER.readValue(configAsString, CrawlConfig.class);

        // then
        assertThat(crawlConfig).isEqualTo(new CrawlConfig("http://something.com", 1, 1));
    }


}