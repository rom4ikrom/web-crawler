package com.monzo.webcrawler.domain.url;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.InvalidUrl;
import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;
import static org.assertj.core.api.Assertions.assertThat;

class UrlNormaliserTest {

    private final UrlNormaliser underTest = new UrlNormaliser();

    @ParameterizedTest
    @CsvSource(value = {
            "http://something.com/product/123?param=other,  http://something.com/product/123?param=other",
            "http://something.com/product/123,              http://something.com/product/123",
            "http://something.com/product/123#section,      http://something.com/product/123",
            "http://SOMETHING.com/PRODUCT/123#section,      http://something.com/PRODUCT/123",
            "http://something.com:8080/test,                http://something.com:8080/test",
            "http://something.com/a/b/../c,                 http://something.com/a/c",
            "http://something.com/a/./b,                    http://something.com/a/b"
    })
    void returnsNormalisedUrlResult(String input, String expected) {
        assertThat(underTest.normalise(input))
                .isEqualTo(new NormalisedUrl(expected, "something.com"));
    }

    @ParameterizedTest
    @NullSource @EmptySource
    @ValueSource(strings = {" ", "http://something.com/foo bar", "ftp://something.com/file", "http:foo"})
    void returnsInvalidUrlResult(String value) {
        assertThat(underTest.normalise(value)).isEqualTo(new InvalidUrl(value));
    }

}