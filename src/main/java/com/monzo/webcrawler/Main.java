package com.monzo.webcrawler;

import com.monzo.webcrawler.domain.url.SameDomainUnvisitedUrlsProvider;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupFacade;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupUrlsExtractor;

import java.time.Duration;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

public class Main {

    static void main(String[] args) {
        String startingUrl = "https://crawlme.monzo.com";
        String domain = Utils.extractDomain(startingUrl);
        Queue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer(startingUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();

        UrlsExtractor urlsExtractor = new JsoupUrlsExtractor(new JsoupFacade());
        UrlNormaliser urlNormaliser = new UrlNormaliser();
        SameDomainUnvisitedUrlsProvider urlProvider = new SameDomainUnvisitedUrlsProvider(urlsExtractor, urlNormaliser);

        long start = System.nanoTime();
        System.out.println("Starting ...");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (visited.size() < 100) {
                executor.submit(() -> {
                    String url = queue.poll();
                    visited.add(url);

                    for (String newUrl : urlProvider.urls(url, domain, visited)) {
                        queue.offer(newUrl);
                    }
                });
            }
        }

        long finish = System.nanoTime();
        System.out.println("Queue size: " + queue.size());
        System.out.println("Finished in " + Duration.ofNanos(finish - start).getSeconds() + " seconds.");
    }



}
