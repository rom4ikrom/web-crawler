package com.monzo.webcrawler.domain.listener;

import com.monzo.webcrawler.domain.model.Page;

public interface PageStoredListener extends AutoCloseable {
    void onPageStored(Page page);
}
