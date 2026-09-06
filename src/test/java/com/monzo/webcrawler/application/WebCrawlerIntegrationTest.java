package com.monzo.webcrawler.application;

import com.monzo.webcrawler.bootstrap.ApplicationContext;
import com.monzo.webcrawler.bootstrap.DependencyOverrides;
import com.monzo.webcrawler.bootstrap.TestFixedCrawlConfigProvider;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.repository.PageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Timeout(value = 10, unit = TimeUnit.SECONDS)
public class WebCrawlerIntegrationTest {

    private ApplicationContext applicationContext;
    private PageRepository pageRepository;

    @Container
    static final GenericContainer<?> wireMock =
            new GenericContainer<>("wiremock/wiremock:3.13.2")
                    .withExposedPorts(8080)
                    .withCopyToContainer(
                            MountableFile.forClasspathResource("wiremock"),
                            "/home/wiremock"
                    )
                    .waitingFor(
                            Wait.forHttp("/__admin/health")
                                    .forStatusCode(200)
                    );

    @BeforeEach
    void setup() {
        DependencyOverrides dependencyOverrides = DependencyOverrides.none();
        CrawlConfig crawlConfig = new CrawlConfig(wiremockUrl(), 1, 10);
        dependencyOverrides.override(new TestFixedCrawlConfigProvider(crawlConfig));
        applicationContext = new ApplicationContext(dependencyOverrides);
        pageRepository = applicationContext.pageRepository();
    }

    @Test
    void crawlsStoresAndPrintsResults() {
        // given
        WebCrawlerApplication webCrawlerApplication = new WebCrawlerApplication(applicationContext);

        // when
        webCrawlerApplication.start();

        // then
        String wiremockUrl = wiremockUrl();
        List<String> expectedCrawledUrls = List.of(
                wiremockUrl, wiremockUrl + "/about", wiremockUrl + "/products", wiremockUrl + "/contact"
        );
        List<String> expectedDiscoveredUrls = List.of(
                wiremockUrl + "/does-not-exist",
                wiremockUrl + "/another-missing-page",
                "https://instagram.com",
                "https://facebook.com",
                "mailto:hello@something.com",
                wiremockUrl + "/product/1",
                wiremockUrl + "/product/2",
                wiremockUrl + "/about",
                wiremockUrl + "/products",
                wiremockUrl + "/contact"
        );
        List<Page> allPages = pageRepository.findAll();
        assertThat(crawledUrls(allPages)).containsExactlyInAnyOrderElementsOf(expectedCrawledUrls);
        assertThat(discoveredUrls(allPages)).containsExactlyInAnyOrderElementsOf(expectedDiscoveredUrls);
    }

    private static String wiremockUrl() {
        return "http://"
                + wireMock.getHost()
                + ":"
                + wireMock.getMappedPort(8080);
    }

    private List<String> crawledUrls(List<Page> pages) {
        return pages.stream().map(Page::url).collect(Collectors.toList());
    }

    private List<String> discoveredUrls(List<Page> pages) {
        return pages.stream().map(Page::urls).flatMap(List::stream).collect(Collectors.toList());
    }

}
