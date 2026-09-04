package com.monzo.webcrawler.domain.url;

import java.util.List;

public interface UrlsExtractor {

    List<String> extract(String url);

}
