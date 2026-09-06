package com.monzo.webcrawler.domain.repository;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;

import java.util.List;
import java.util.Optional;

public interface PageRepository {

    void store(Page page);

    void addListener(PageStoredListener pageStoredListener);

}
