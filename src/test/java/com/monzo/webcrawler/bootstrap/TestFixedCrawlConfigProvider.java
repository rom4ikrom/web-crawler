package com.monzo.webcrawler.bootstrap;

import com.monzo.webcrawler.application.CrawlConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestFixedCrawlConfigProvider implements CrawlConfigProvider {

    @NonNull
    private final CrawlConfig crawlConfig;

    @Override
    public CrawlConfig crawlConfig() {
        return crawlConfig;
    }

}
