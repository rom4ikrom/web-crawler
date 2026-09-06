package com.monzo.webcrawler.bootstrap;

import com.monzo.webcrawler.domain.model.PageIdGenerator;
import com.monzo.webcrawler.domain.repository.PageRepository;
import com.monzo.webcrawler.domain.url.UrlNormaliser;
import com.monzo.webcrawler.domain.url.UrlsExtractor;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupFacade;
import com.monzo.webcrawler.infrastructure.jsoup.JsoupUrlsExtractor;
import com.monzo.webcrawler.infrastructure.repository.InMemoryPageRepository;
import lombok.Getter;
import tools.jackson.databind.json.JsonMapper;

@Getter
public class ApplicationContext {

    private final CrawlConfigProvider crawlConfigParser;
    private final UrlNormaliser urlNormaliser;
    private final JsoupFacade jsoupFacade;
    private final UrlsExtractor urlsExtractor;
    private final PageRepository pageRepository;
    private final PageIdGenerator pageIdGenerator;

    public ApplicationContext(DependencyOverrides dependencyOverrides) {
        JsonMapper jsonMapper = new JsonMapper();

        this.crawlConfigParser = dependencyOverrides.maybeCrawlConfigProvider()
                .orElseGet(() -> new ResourceBasedCrawlConfigProvider(jsonMapper));

        this.urlNormaliser = new UrlNormaliser();
        this.jsoupFacade = new JsoupFacade();
        this.urlsExtractor = new JsoupUrlsExtractor(this.jsoupFacade);
        this.pageRepository = new InMemoryPageRepository();
        this.pageIdGenerator = new PageIdGenerator();
    }

}
