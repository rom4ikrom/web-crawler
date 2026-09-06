package com.monzo.webcrawler.application;

import com.monzo.webcrawler.domain.model.IdGenerator;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlProcessor;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;

@RequiredArgsConstructor
public class PersistenceAwareUrlProcessor implements UrlProcessor {

    private final UrlsExtractor urlsExtractor;
    private final UrlNormaliser urlNormaliser;
    private final PageRepository pageRepository;
    private final IdGenerator idGenerator;

    @Override
    public List<NormalisedUrl> process(NormalisedUrl url) {
        String target = url.value();
        List<String> extractedUrls = urlsExtractor.extract(target);

        Page page = Page.builder()
                .id(idGenerator.nextId())
                .url(target)
                .urls(List.copyOf(extractedUrls))
                .build();
        pageRepository.store(page);

        return extractedUrls.stream()
                .map(urlNormaliser::normalise)
                .filter(UrlNormalisationResult::isNormalised)
                .map(NormalisedUrl.class::cast)
                .collect(Collectors.toList());

    }

}
