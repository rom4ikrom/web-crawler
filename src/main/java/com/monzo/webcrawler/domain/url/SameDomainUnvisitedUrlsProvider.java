package com.monzo.webcrawler.domain.url;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class SameDomainUnvisitedUrlsProvider {

    private final UrlsExtractor urlsExtractor;
    private final UrlNormaliser urlNormaliser;

    public Set<String> urls(String target, String domain, Set<String> visited) {
        Set<String> urls = new HashSet<>();
        Set<String> extractedUrls = urlsExtractor.extract(target);
        for (String extractedUrl : extractedUrls) {
            NormalisedUrl normalisedUrl = urlNormaliser.normalise(extractedUrl);
            if (normalisedUrl.hasSameDomainAs(domain) && !visited.contains(normalisedUrl.value())) {
                urls.add(normalisedUrl.value());
            }
        }
        return urls;
    }

}
