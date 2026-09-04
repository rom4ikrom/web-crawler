package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class CrawlTracker {

    private final Set<String> visited = ConcurrentHashMap.newKeySet();
    private final AtomicInteger pending = new AtomicInteger();
    private final Object monitor = new Object();

    private final String startUrlDomain;

    public synchronized boolean submit(NormalisedUrl normalisedUrl) {
        if (!normalisedUrl.hasSameDomainAs(startUrlDomain)) {
            return false;
        }

        // TODO add optional limit
//        if (visited.size() >= 100) {
//            return false;
//        }

        String url = normalisedUrl.value();
        return visited.add(url);
    }

    public void submitted() {
        pending.incrementAndGet();
    }

    public void complete() {
        if (pending.decrementAndGet() == 0) {
            synchronized (monitor) {
                monitor.notify();
            }
        }
    }

    public void awaitCompletion() throws InterruptedException {
        synchronized (monitor) {
            while (pending.get() > 0) {
                monitor.wait();
            }
        }
    }

}
