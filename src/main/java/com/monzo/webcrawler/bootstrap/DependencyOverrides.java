package com.monzo.webcrawler.bootstrap;

import java.util.Optional;

public class DependencyOverrides {

    private CrawlConfigProvider crawlConfigProvider;

    private DependencyOverrides() {}

    public static DependencyOverrides none() {
        return new DependencyOverrides();
    }

    public void override(CrawlConfigProvider crawlConfigProvider) {
        this.crawlConfigProvider = crawlConfigProvider;
    }

    public Optional<CrawlConfigProvider> maybeCrawlConfigProvider() {
        return Optional.ofNullable(crawlConfigProvider);
    }
}
