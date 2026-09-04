package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.url.UrlProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;

public class WebCrawler implements Closeable {

    private static final Logger LOG = LogManager.getLogger();

    private final CrawlTracker crawlTracker;
    private final ExecutorService executorService;

    private final UrlProcessor processor;

    public WebCrawler(int workerCount,
                      CrawlTracker crawlTracker,
                      UrlProcessor processor) {
        this.crawlTracker = crawlTracker;
        ThreadFactory threadFactory = Thread.ofVirtual()
                .name("crawler-", 0)
                .factory();
        this.executorService = Executors.newFixedThreadPool(workerCount, threadFactory);
        this.processor = processor;
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
        try {
            processor.process(url).forEach(this::submit);
        } finally {
            crawlTracker.complete();
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
