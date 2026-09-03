package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.*;

public class WebCrawler implements Closeable {

    private final int workerCount;
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
        this.workerCount = workerCount;
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
        for (int i = 0; i < workerCount; i++) {
            executorService.submit(new CrawlTask(urlsExtractor, urlNormaliser, pageRepository, crawlTracker));
        }

        crawlTracker.submit(startUrl);
        crawlTracker.awaitCompletion();
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
