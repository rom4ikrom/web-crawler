package com.monzo.webcrawler;

import com.monzo.webcrawler.application.CrawlTracker;
import com.monzo.webcrawler.application.PersistenceAwareUrlProcessor;
import com.monzo.webcrawler.application.WebCrawler;
import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.PageIdGenerator;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupFacade;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupUrlsExtractor;
import com.monzo.webcrawler.infrastructure.listener.ConsoleOutputPageStoredConsumer;
import com.monzo.webcrawler.infrastructure.repository.InMemoryPageRepository;

import java.time.Duration;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;

public class Application {

    static void main(String[] args) {
        String startUrl = "https://crawlme.monzo.com";
        UrlNormaliser urlNormaliser = new UrlNormaliser();
        NormalisedUrl normalisedStartUrl = urlNormaliser.normalisedUrlOrThrow(startUrl);
        CrawlTracker crawlTracker = new CrawlTracker(normalisedStartUrl.domain());
        UrlsExtractor urlsExtractor = new JsoupUrlsExtractor(new JsoupFacade());

        PageRepository pageRepository = new InMemoryPageRepository();
        PageIdGenerator pageIdGenerator = new PageIdGenerator();
        PersistenceAwareUrlProcessor urlProcessor = new PersistenceAwareUrlProcessor(urlsExtractor, urlNormaliser, pageRepository, pageIdGenerator);

        long start = System.nanoTime();
        System.out.println("Starting ...");

        try (
                WebCrawler webCrawler = new WebCrawler(5, crawlTracker, urlProcessor);
                PageStoredListener pageStoredListener = new ConsoleOutputPageStoredConsumer();
        ) {
            pageRepository.addListener(pageStoredListener);
            pageStoredListener.start();
            webCrawler.start(normalisedStartUrl);
        } catch (Exception ex) {
            System.exit(1);
        }

        long finish = System.nanoTime();
        System.out.println("Finished in " + Duration.ofNanos(finish - start).getSeconds() + " seconds.");
    }



}
