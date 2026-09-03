package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.*;

public class WebCrawler implements Closeable {

    private static final Logger LOG = LogManager.getLogger();

    private final CrawlTracker crawlTracker;
    private final ExecutorService executorService;

    private final UrlsExtractor urlsExtractor;
    private final UrlNormaliser urlNormaliser;
    private final PageRepository pageRepository;

    public WebCrawler(int workerCount,
                      CrawlTracker crawlTracker,
                      UrlsExtractor urlsExtractor,
                      UrlNormaliser urlNormaliser,
                      PageRepository pageRepository) {
        this.crawlTracker = crawlTracker;
        ThreadFactory threadFactory = Thread.ofVirtual()
                .name("crawler-", 0)
                .factory();
        this.executorService = Executors.newFixedThreadPool(workerCount, threadFactory);

        this.urlsExtractor = urlsExtractor;
        this.urlNormaliser = urlNormaliser;
        this.pageRepository = pageRepository;
    }

    public void start(NormalisedUrl startUrl) throws InterruptedException {
        submit(startUrl);
        crawlTracker.awaitCompletion();
    }

    private void submit(NormalisedUrl normalisedUrl) {
        if (!crawlTracker.submit(normalisedUrl)) {
            return;
        }
        crawlTracker.submitted();
        try {
            executorService.submit(() -> process(normalisedUrl));
        } catch (RejectedExecutionException ex) {
            crawlTracker.complete();
            throw ex;
        }
    }

    private void process(NormalisedUrl url) {
        String target = url.value();
        LOG.info("Processing: {}", target);
        try {
            Set<String> extractedUrls = urlsExtractor.extract(target);

            Page page = new Page(UUID.randomUUID().toString(), target, List.copyOf(extractedUrls));
            pageRepository.store(page);

            for (String extractedUrl : extractedUrls) {
                UrlNormalisationResult result = urlNormaliser.normalise(extractedUrl);
                switch (result) {
                    case NormalisedUrl normalisedUrl -> submit(normalisedUrl);
                    default -> {}
                }
            }
        } finally {
            crawlTracker.complete();
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
