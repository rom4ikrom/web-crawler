package com.monzo.webcrawler.domain.url;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.*;

@RequiredArgsConstructor
public class SameDomainUnvisitedUrlsProvider {

    private final UrlsExtractor urlsExtractor;
    private final UrlNormaliser urlNormaliser;

    public Set<String> urls(String target, String domain, Set<String> visited) {
        Set<String> urls = new HashSet<>();
        try {
            Set<String> extractedUrls = urlsExtractor.extract(target);
            for (String extractedUrl : extractedUrls) {
                UrlNormalisationResult result = urlNormaliser.normalise(extractedUrl);
                switch (result) {
                    case NormalisedUrl normalisedUrl -> processNormalisedUrl(normalisedUrl, domain, visited, urls);
                    default -> {}
                }
            }
            return urls;
        } catch (Exception ex) {
            return Set.of();
        }
    }

    private void processNormalisedUrl(NormalisedUrl normalisedUrl, String domain, Set<String> visited, Set<String> urls) {
        if (normalisedUrl.hasSameDomainAs(domain) && !visited.contains(normalisedUrl.value())) {
            urls.add(normalisedUrl.value());
        }
    }

}
