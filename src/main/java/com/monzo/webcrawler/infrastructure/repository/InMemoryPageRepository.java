package com.monzo.webcrawler.infrastructure.repository;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class InMemoryPageRepository implements PageRepository {

    private final ConcurrentHashMap<String, Page> store;
    private final List<PageStoredListener> pageStoredListeners;

    @Override
    public void store(Page page) {
        store.putIfAbsent(page.id(), page);
        notifyListeners(page);
    }

    @Override
    public void addListener(PageStoredListener pageStoredListener) {
        pageStoredListeners.add(pageStoredListener);
    }

    private void notifyListeners(Page page) {
        for(PageStoredListener listener : pageStoredListeners) {
            listener.onPageStored(page);
        }
    }

}
