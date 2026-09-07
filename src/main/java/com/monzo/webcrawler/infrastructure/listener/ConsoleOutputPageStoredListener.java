package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleOutputPageStoredListener implements PageStoredListener, AutoCloseable {

    private static final Logger LOG = LogManager.getLogger(ConsoleOutputPageStoredListener.class);

    private static final Page POISON_PILL = Page.builder()
            .id("poison-pill")
            .crawledUrl(new NormalisedUrl("https://poison-pill.com", "poison-pill.com"))
            .sameDomainUrls(List.of())
            .otherDomainUrls(List.of())
            .invalidUrls(List.of())
            .build();

    private final LinkedBlockingQueue<Page> queue;
    private Thread thread;

    public ConsoleOutputPageStoredListener() {
        this(new LinkedBlockingQueue<>());
    }

    @Override
    public void onPageStored(Page page) {
        put(page);
    }

    public void start() {
        thread = Thread.ofVirtual()
                .name("console-consumer")
                .start(this::consumeLoop);
    }

    private void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Page page = queue.take();

                if (page == POISON_PILL) {
                    LOG.info("Poison pill, stopping console thead...");
                    return;
                }

                List<String> urls = Stream.of(page.sameDomainUrls(), page.otherDomainUrls(), page.invalidUrls())
                        .flatMap(List::stream)
                        .map(UrlNormalisationResult::value)
                        .toList();
                LOG.info("Discovered {} URLs for: {}", urls.size(), page.crawledUrl().value());
                for (int i = 1; i <= urls.size(); i++) {
                    LOG.info("{}. {}", i, urls.get(i - 1));
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void completePublishing() {
        put(POISON_PILL);
    }

    @Override
    public void close() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void awaitCompletion() throws InterruptedException {
        if (thread != null) {
            thread.join();
        }
    }

    private void put(Page page) {
        try {
            queue.put(page);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing page event", e);
        }
    }

}
