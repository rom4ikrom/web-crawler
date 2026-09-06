package com.monzo.webcrawler.domain.listener;

import com.monzo.webcrawler.domain.model.Page;

public interface PageStoredListener {
    void onPageStored(Page page);
}
