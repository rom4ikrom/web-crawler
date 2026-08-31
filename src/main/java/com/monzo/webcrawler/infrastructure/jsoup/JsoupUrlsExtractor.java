package com.monzo.webcrawler.infrastructure.jsoup;

import com.monzo.webcrawler.domain.url.UrlsExtractor;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JsoupUrlsExtractor implements UrlsExtractor {

    private final JsoupFacade jsoupFacade;

    @Override
    public Set<String> extract(String url) {
        Document document = jsoupFacade.document(url);
        return document.select("a[href]").stream()
                .map(link -> link.attr("abs:href"))
                .collect(Collectors.toSet());
    }
}
