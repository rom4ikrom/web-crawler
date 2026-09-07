package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class TestUrlsOnlyPageStoredListener implements PageStoredListener {

    private final List<String> crawledUrls;
    private final List<String> foundUrls;

    public TestUrlsOnlyPageStoredListener() {
        this.crawledUrls = new ArrayList<>();
        this.foundUrls = new ArrayList<>();
    }

    @Override
    public synchronized void onPageStored(Page page) {
        crawledUrls.add(page.crawledUrl().value());
        List<String> urls = Stream.of(page.sameDomainUrls(), page.otherDomainUrls(), page.invalidUrls())
                .flatMap(List::stream)
                .map(UrlNormalisationResult::value)
                .toList();
        foundUrls.addAll(urls);
    }
}