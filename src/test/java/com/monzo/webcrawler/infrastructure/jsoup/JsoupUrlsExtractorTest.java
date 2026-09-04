package com.monzo.webcrawler.infrastructure.jsoup;

import com.monzo.webcrawler.TestResources;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsoupUrlsExtractorTest {

    private static final String BASE_URL = "https://something.com";

    private JsoupUrlsExtractor underTest;

    @Mock
    private JsoupFacade jsoupFacade;

    @BeforeEach
    void setup() {
        underTest = new JsoupUrlsExtractor(jsoupFacade);
    }

    @Test
    void returnsAbsoluteLinks() {
        // given
        Document document = Jsoup.parse(
                TestResources.read("/infrastructure/jsoup/document.html"),
                BASE_URL);
        when(jsoupFacade.document(BASE_URL)).thenReturn(document);

        // when
        List<String> result = underTest.extract(BASE_URL);

        // then
        List<String> expected = List.of(
                withBaseUrl("/home"),
                "https://example.com/external",
                withBaseUrl("/products/1"),
                withBaseUrl("/products/2"),
                withBaseUrl("#section")
        );
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    private static String withBaseUrl(String url) {
        return BASE_URL + url;
    }

}