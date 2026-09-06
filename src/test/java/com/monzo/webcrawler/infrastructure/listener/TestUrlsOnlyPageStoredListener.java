package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestUrlsOnlyPageStoredListener implements PageStoredListener {

    private List<String> crawledUrls;
    private List<String> discoveredUrls;

    public TestUrlsOnlyPageStoredListener() {
        this.crawledUrls = new ArrayList<>();
        this.discoveredUrls = new ArrayList<>();
    }

    @Override
    public synchronized void onPageStored(Page page) {
        crawledUrls.add(page.url());
        discoveredUrls.addAll(page.urls());
    }
}