package com.monzo.webcrawler.domain.repository;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;

import java.util.Optional;

public interface PageRepository {

    void store(Page page);

    Optional<Page> maybePage(String id);

    void addListener(PageStoredListener pageStoredListener);

}
