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
                "something.com", urlsExtractor, urlNormaliser, pageRepository, idGenerator
        );
    }

    @Test
    void shouldStoreAllUrlsAndReturnSameDomainNormalisedOnly() {
        // given
        UrlNormalisationResult.NormalisedUrl startUrl = new NormalisedUrl("http://something.com", "something.com");
        List<String> extractedUrls = List.of(
                "http://something.com/home",
                "mailto:hello@something.com",
                "http://something.com/products",
                "https://instagram.com");
        when(urlsExtractor.extract("http://something.com")).thenReturn(extractedUrls);
        when(idGenerator.nextId()).thenReturn("a-page-id");

        // and
        NormalisedUrl firstSameDomainNormalisedUrl = new NormalisedUrl("http://something.com/home", "something.com");
        NormalisedUrl secondSameDomainNormalisedUrl = new NormalisedUrl("http://something.com/products", "something.com");
        InvalidUrl invalidUrl = new InvalidUrl("mailto:hello@something.com");
        NormalisedUrl otherDomainNormalisedUrl = new NormalisedUrl("https://instagram.com", "instagram.com");
        when(urlNormaliser.normalise("http://something.com/home")).thenReturn(firstSameDomainNormalisedUrl);
        when(urlNormaliser.normalise("mailto:hello@something.com")).thenReturn(invalidUrl);
        when(urlNormaliser.normalise("http://something.com/products")).thenReturn(secondSameDomainNormalisedUrl);
        when(urlNormaliser.normalise("https://instagram.com")).thenReturn(otherDomainNormalisedUrl);

        // when
        List<NormalisedUrl> result = underTest.process(startUrl);

        // then
        List<NormalisedUrl> sameDomainNormalisedUrls = List.of(firstSameDomainNormalisedUrl, secondSameDomainNormalisedUrl);
        assertThat(result).containsExactlyInAnyOrderElementsOf(sameDomainNormalisedUrls);

        // and
        Page expectedPage = Page.builder()
                .id("a-page-id")
                .crawledUrl(startUrl)
                .sameDomainUrls(sameDomainNormalisedUrls)
                .otherDomainUrls(List.of(otherDomainNormalisedUrl))
                .invalidUrls(List.of(invalidUrl))
                .build();
        verify(pageRepository, times(1)).store(expectedPage);
    }

}