package com.monzo.webcrawler.infrastructure.repository;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPageRepository implements PageRepository {

    private final ConcurrentHashMap<String, Page> store;
    private final List<PageStoredListener> pageStoredListeners;

    public InMemoryPageRepository() {
        this.store = new ConcurrentHashMap<>();
        this.pageStoredListeners = new ArrayList<>();
    }

    @Override
    public void store(Page page) {
        store.putIfAbsent(page.id(), page);
        notifyListeners(page);
    }

    @Override
    public Optional<Page> maybePage(String id) {
        return Optional.ofNullable(store.get(id));
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
