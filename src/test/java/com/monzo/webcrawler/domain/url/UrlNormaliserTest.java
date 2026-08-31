package com.monzo.webcrawler.domain.url;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.*;

class UrlNormaliserTest {

    private final UrlNormaliser underTest = new UrlNormaliser();

    @ParameterizedTest
    @CsvSource(value = {
            "http://something.com/product/123?param=other,http://something.com/product/123?param=other",
            "http://something.com/product/123,http://something.com/product/123",
            "http://something.com/product/123#section,http://something.com/product/123"
    })
    void returnsNormalisedUrl(String input, String expected) {
        // when
        NormalisedUrl result = underTest.normalise(input);

        // then
        assertThat(result).isEqualTo(new NormalisedUrl(expected, "something.com"));
    }

    @ParameterizedTest
    @NullSource @EmptySource
    @ValueSource(strings = " ")
    void throwsExceptionWhenProvidedValueIsInvalid(String value) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> underTest.normalise(value))
                .withMessage("Provided string must not be null, empty or whitespace.");
    }

    @Test
    void throwsExceptionWhenURIHasInvalidSyntax() {
        assertThatRuntimeException()
                .isThrownBy(() -> underTest.normalise("http://something.com/foo bar"))
                .withRootCauseExactlyInstanceOf(URISyntaxException.class);
    }

    @Test
    void throwsExceptionWhenURIHasNotAcceptableScheme() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> underTest.normalise("ftp://example.com/file"))
                .withMessage("Provided URI ftp://example.com/file has not acceptable scheme.");
    }

}