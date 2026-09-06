package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.model.Page;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ConsoleOutputPageStoredConsumerTest {

    private ConsoleOutputPageStoredConsumer underTest;
    private InMemoryAppender inMemoryAppender;

    @BeforeEach
    void setup() {
        underTest = new ConsoleOutputPageStoredConsumer();
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
                .url("http://something.com")
                .urls(List.of("http://something.com/products", "http://something.com/about"))
                .build();
        Page anotherPage = Page.builder()
                .id("another-page-id")
                .url("http://something.com/products")
                .urls(List.of("http://something.com/products/1"))
                .build();

        // when
        underTest.onPageStored(page);
        underTest.onPageStored(anotherPage);
        underTest.completePublishing();

        // then
        await().atMost(Duration.ofSeconds(5)).until(() -> inMemoryAppender.size() == 6);
        assertThat(inMemoryAppender.eventMessages()).containsExactlyElementsOf(List.of(
                "Discovered 2 URLs for: http://something.com",
                "1. http://something.com/products",
                "2. http://something.com/about",
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