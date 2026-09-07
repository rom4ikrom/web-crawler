package com.monzo.webcrawler.application;

import com.monzo.webcrawler.bootstrap.ApplicationContext;
import com.monzo.webcrawler.domain.model.PageIdGenerator;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import com.monzo.webcrawler.infrastructure.listener.ConsoleOutputPageStoredListener;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

@RequiredArgsConstructor
public class WebCrawlerApplication {

    private static final Logger LOG = LogManager.getLogger(WebCrawlerApplication.class);

    private final ApplicationContext applicationContext;

    public void start() {

        CrawlConfig crawlConfig = applicationContext.crawlConfigParser().crawlConfig();
        UrlNormaliser urlNormaliser = applicationContext.urlNormaliser();
        UrlNormalisationResult.NormalisedUrl normalisedStartUrl = urlNormaliser.normalisedUrlOrThrow(crawlConfig.startUrl());

        UrlsExtractor urlsExtractor = applicationContext.urlsExtractor();
        PageRepository pageRepository = applicationContext.pageRepository();
        PageIdGenerator pageIdGenerator = applicationContext.pageIdGenerator();

        CrawlTracker crawlTracker = new CrawlTracker(normalisedStartUrl.domain(), crawlConfig.numberOfLinksToDiscover());
        DefaultUrlProcessor urlProcessor = new DefaultUrlProcessor(urlsExtractor, urlNormaliser, pageRepository, pageIdGenerator);

        long start = System.nanoTime();
        LOG.info("Starting Web Crawler for {}", normalisedStartUrl.value());

        try (
                WebCrawler webCrawler = new WebCrawler(crawlConfig.numberOfThreads(), crawlTracker, urlProcessor);
                ConsoleOutputPageStoredListener console = new ConsoleOutputPageStoredListener();
        ) {
            pageRepository.addListener(console);
            console.start();
            webCrawler.start(normalisedStartUrl);
            console.completePublishing();
            console.awaitCompletion();
        } catch (Exception ex) {
            LOG.error("WebCrawler failed due to: ", ex);
            throw new RuntimeException(ex);
        }

        long finish = System.nanoTime();
        LOG.info("Finished in {} seconds.", Duration.ofNanos(finish - start).getSeconds());
    }

}
