package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.model.IdGenerator;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult.InvalidUrl;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUrlProcessorTest {

    private DefaultUrlProcessor underTest;

    @Mock
    private UrlsExtractor urlsExtractor;
    @Mock
    private UrlNormaliser urlNormaliser;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private IdGenerator idGenerator;

    @BeforeEach
    void setup() {
        underTest = new DefaultUrlProcessor(
                urlsExtractor, urlNormaliser, pageRepository, idGenerator
        );
    }

    @Test
    void shouldStoreAllUrlsAndReturnNormalisedOnly() {
        // given
        UrlNormalisationResult.NormalisedUrl startUrl = new NormalisedUrl("http://something.com", "something.com");
        List<String> extractedUrls = List.of("http://something.com/home", "mailto:hello@something.com", "http://something.com/products");
        when(urlsExtractor.extract("http://something.com")).thenReturn(extractedUrls);
        when(idGenerator.nextId()).thenReturn("a-page-id");
        when(urlNormaliser.normalise("http://something.com/home"))
                .thenReturn(new NormalisedUrl("http://something.com/home", "something.com"));
        when(urlNormaliser.normalise("mailto:hello@something.com"))
                .thenReturn(InvalidUrl.instance());
        when(urlNormaliser.normalise("http://something.com/products"))
                .thenReturn(new NormalisedUrl("http://something.com/products", "something.com"));

        // when
        List<NormalisedUrl> result = underTest.process(startUrl);

        // then
        assertThat(result).containsExactlyInAnyOrder(
                new NormalisedUrl("http://something.com/home", "something.com"),
                new NormalisedUrl("http://something.com/products", "something.com")
        );

        // and
        Page expectedPage = Page.builder()
                .id("a-page-id")
                .url("http://something.com")
                .urls(extractedUrls)
                .build();
        verify(pageRepository, times(1)).store(expectedPage);
    }

}