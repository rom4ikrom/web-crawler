package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult.InvalidUrl;
import com.monzo.webcrawler.util.InMemoryAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ConsoleOutputPageStoredListenerTest {

    private static final String DOMAIN = "something.com";

    private ConsoleOutputPageStoredListener underTest;
    private InMemoryAppender inMemoryAppender;

    @BeforeEach
    void setup() {
        underTest = new ConsoleOutputPageStoredListener();
        inMemoryAppender = new InMemoryAppender();
        inMemoryAppender.start();
        Logger logger = logger();
        logger.addAppender(inMemoryAppender);
        logger.setLevel(Level.ALL);

        underTest.start();
    }

    @AfterEach
    void cleanUp() {
        logger().removeAppender(inMemoryAppender);
        inMemoryAppender.stop();
        inMemoryAppender.clear();

        underTest.close();
    }

    @Test
    void shouldLogDiscoveredUrlsAndStopOnPoisonPill() {
        // given
        Page page = Page.builder()
                .id("a-page-id")
                .crawledUrl(new NormalisedUrl("http://something.com", DOMAIN))
                .sameDomainUrls(List.of(
                        new NormalisedUrl("http://something.com/products", DOMAIN),
                        new NormalisedUrl("http://something.com/about", DOMAIN)))
                .otherDomainUrls(List.of(
                        new NormalisedUrl("http://other.com", "other.com")
                ))
                .invalidUrls(List.of(new InvalidUrl("foo:bar")))
                .build();
        Page anotherPage = Page.builder()
                .id("another-page-id")
                .crawledUrl(new NormalisedUrl("http://something.com/products", DOMAIN))
                .sameDomainUrls(List.of(new NormalisedUrl("http://something.com/products/1", DOMAIN)))
                .otherDomainUrls(List.of())
                .invalidUrls(List.of())
                .build();

        // when
        underTest.onPageStored(page);
        underTest.onPageStored(anotherPage);
        underTest.completePublishing();

        // then
        await().atMost(Duration.ofSeconds(5)).until(() -> inMemoryAppender.size() == 8);
        assertThat(inMemoryAppender.eventMessages()).containsExactlyElementsOf(List.of(
                "Discovered 4 URLs for: http://something.com",
                "1. http://something.com/products",
                "2. http://something.com/about",
                "3. http://other.com",
                "4. foo:bar",
                "Discovered 1 URLs for: http://something.com/products",
                "1. http://something.com/products/1",
                "Poison pill, stopping console thead..."
        ));
    }

    private Logger logger() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        return (Logger) context.getLogger(underTest.getClass());
    }

}