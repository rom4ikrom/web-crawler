package com.monzo.webcrawler;

import com.monzo.webcrawler.domain.url.SameDomainUnvisitedUrlsProvider;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupFacade;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupUrlsExtractor;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

        while (!queue.isEmpty()) {
            String url = queue.poll();
            visited.add(url);

            for (String newUrl : urlProvider.urls(url, domain, visited)) {
                queue.offer(newUrl);
            }
        }

    }



}
