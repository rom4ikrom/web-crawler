package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.model.PageIdGenerator;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupFacade;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupUrlsExtractor;
import com.monzo.webcrawler.infrastructure.listener.ConsoleOutputPageStoredConsumer;
import com.monzo.webcrawler.infrastructure.repository.InMemoryPageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

public class Application {

    private static final Logger LOG = LogManager.getLogger();

    public static void start() {
        JsonMapper jsonMapper = new JsonMapper();
        CrawlConfigParser crawlConfigParser = new CrawlConfigParser(jsonMapper);
        CrawlConfig crawlConfig = crawlConfigParser.crawlConfig();
        UrlNormaliser urlNormaliser = new UrlNormaliser();
        UrlNormalisationResult.NormalisedUrl normalisedStartUrl = urlNormaliser.normalisedUrlOrThrow(crawlConfig.startUrl());
        CrawlTracker crawlTracker = new CrawlTracker(normalisedStartUrl.domain(), crawlConfig.numberOfLinksToDiscover());
        UrlsExtractor urlsExtractor = new JsoupUrlsExtractor(new JsoupFacade());

        PageRepository pageRepository = new InMemoryPageRepository();
        PageIdGenerator pageIdGenerator = new PageIdGenerator();
        PersistenceAwareUrlProcessor urlProcessor = new PersistenceAwareUrlProcessor(urlsExtractor, urlNormaliser, pageRepository, pageIdGenerator);

        long start = System.nanoTime();
        LOG.info("Starting Web Crawler for {}", normalisedStartUrl.value());

        try (
                WebCrawler webCrawler = new WebCrawler(crawlConfig.numberOfThreads(), crawlTracker, urlProcessor);
                ConsoleOutputPageStoredConsumer console = new ConsoleOutputPageStoredConsumer();
        ) {
            pageRepository.addListener(console);
            console.start();
            webCrawler.start(normalisedStartUrl);
            console.completePublishing();
            console.awaitCompletion();
        } catch (Exception ex) {
            System.exit(1);
        }

        long finish = System.nanoTime();
        LOG.info("Finished in {} seconds.", Duration.ofNanos(finish - start).getSeconds());
    }

}
