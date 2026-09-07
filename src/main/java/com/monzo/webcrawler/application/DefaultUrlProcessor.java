package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.model.IdGenerator;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult.InvalidUrl;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlProcessor;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;

@RequiredArgsConstructor
public class DefaultUrlProcessor implements UrlProcessor {

    private final String startUrlDomain;
    private final UrlsExtractor urlsExtractor;
    private final UrlNormaliser urlNormaliser;
    private final PageRepository pageRepository;
    private final IdGenerator idGenerator;

    @Override
    public List<NormalisedUrl> process(NormalisedUrl url) {
        String target = url.value();
        List<String> extractedUrls = urlsExtractor.extract(target);

        List<UrlNormalisationResult> urlNormalisationResults = extractedUrls.stream()
                .map(urlNormaliser::normalise)
                .toList();

        List<NormalisedUrl> allNormalisedUrls = new ArrayList<>();
        List<InvalidUrl> nonEmptyInvalidUrls = new ArrayList<>();

        for (UrlNormalisationResult result : urlNormalisationResults) {
            switch (result) {
                case NormalisedUrl normalisedUrl -> allNormalisedUrls.add(normalisedUrl);
                case InvalidUrl invalidUrl -> addIfNotNullAndNotBlank(nonEmptyInvalidUrls, invalidUrl);
            }
        }

        List<NormalisedUrl> sameDomainNormalisedUrls = allNormalisedUrls.stream()
                .filter(normalisedUrl -> normalisedUrl.hasSameDomainAs(startUrlDomain))
                .toList();

        List<NormalisedUrl> otherDomainNormalisedUrls = allNormalisedUrls.stream()
                .filter(normalisedUrl -> !normalisedUrl.hasSameDomainAs(startUrlDomain))
                .toList();

        Page page = Page.builder()
                .id(idGenerator.nextId())
                .crawledUrl(url)
                .sameDomainUrls(sameDomainNormalisedUrls)
                .otherDomainUrls(otherDomainNormalisedUrls)
                .invalidUrls(nonEmptyInvalidUrls)
                .build();
        pageRepository.store(page);
        return sameDomainNormalisedUrls;
    }

    private void addIfNotNullAndNotBlank(List<InvalidUrl> values, InvalidUrl invalidUrl) {
        if (!invalidUrl.isNullOrBlank()) {
            values.add(invalidUrl);
        }
    }

}
