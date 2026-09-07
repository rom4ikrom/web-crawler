package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class StatsTracker implements PageStoredListener {

    private static final Logger LOG = LogManager.getLogger(StatsTracker.class);

    private final AtomicInteger urlsCrawled;
    private final AtomicInteger sameDomainUrlsFound;
    private final AtomicInteger otherDomainUrlsFound;
    private final AtomicInteger invalidUrlsFound;

    public StatsTracker() {
        this.urlsCrawled = new AtomicInteger();
        this.sameDomainUrlsFound = new AtomicInteger();
        this.otherDomainUrlsFound = new AtomicInteger();
        this.invalidUrlsFound = new AtomicInteger();
    }

    @Override
    public void onPageStored(Page page) {
        urlsCrawled.incrementAndGet();
        sameDomainUrlsFound.addAndGet(page.sameDomainUrls().size());
        otherDomainUrlsFound.addAndGet(page.otherDomainUrls().size());
        invalidUrlsFound.addAndGet(page.invalidUrls().size());
    }

    public void logStats() {
        LOG.info("Total number of URLs crawled:             {}.", urlsCrawled);
        LOG.info("Total number of same domain URLs found:   {}.", sameDomainUrlsFound);
        LOG.info("Total number of other domain URLs found:  {}.", otherDomainUrlsFound);
        LOG.info("Total number of invalid URLs found:       {}.", invalidUrlsFound);
        LOG.info("Total number of URLs found:               {}.", allUrlsCount());
    }

    private int allUrlsCount() {
        return sameDomainUrlsFound.get() + otherDomainUrlsFound.get() + invalidUrlsFound.get();
    }
}
