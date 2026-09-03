package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;

@RequiredArgsConstructor
public class CrawlTask implements Runnable {

    @NonNull
    private final UrlsExtractor urlsExtractor;
    @NonNull
    private final UrlNormaliser urlNormaliser;
    @NonNull
    private final PageRepository pageRepository;
    @NonNull
    private final CrawlTracker crawlTracker;

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String url = crawlTracker.take();
                    process(url);
                } finally {
                    crawlTracker.complete();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void process(String target) {
        Set<String> extractedUrls = urlsExtractor.extract(target);

        Page page = new Page(UUID.randomUUID().toString(), target, List.copyOf(extractedUrls));
        pageRepository.store(page);

        for (String extractedUrl : extractedUrls) {
            UrlNormalisationResult result = urlNormaliser.normalise(extractedUrl);
            switch (result) {
                case NormalisedUrl normalisedUrl -> crawlTracker.submit(normalisedUrl);
                default -> {}
            }
        }
    }

}
