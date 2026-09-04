package com.monzo.webcrawler.domain.model;

import java.util.UUID;

public class PageIdGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
