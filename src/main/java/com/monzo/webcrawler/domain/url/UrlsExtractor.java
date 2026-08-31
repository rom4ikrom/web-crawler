package com.monzo.webcrawler.domain.url;

import java.util.Set;

public interface UrlsExtractor {

    Set<String> extract(String url);

}
