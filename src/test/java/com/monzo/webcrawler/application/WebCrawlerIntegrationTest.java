package com.monzo.webcrawler.application;

import com.monzo.webcrawler.bootstrap.ApplicationContext;
import com.monzo.webcrawler.bootstrap.DependencyOverrides;
import com.monzo.webcrawler.bootstrap.TestFixedCrawlConfigProvider;
import com.monzo.webcrawler.infrastructure.listener.TestUrlsOnlyPageStoredListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Testcontainers
@Timeout(value = 10, unit = TimeUnit.SECONDS)
public class WebCrawlerIntegrationTest {

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

    @Test
    void crawlsAndStoresResults() {
        // given
        DependencyOverrides dependencyOverrides = DependencyOverrides.none();
        CrawlConfig crawlConfig = new CrawlConfig(wiremockUrl(), 1, 10);
        dependencyOverrides.override(new TestFixedCrawlConfigProvider(crawlConfig));
        ApplicationContext applicationContext = new ApplicationContext(dependencyOverrides);
        WebCrawlerApplication webCrawlerApplication = new WebCrawlerApplication(applicationContext);
        TestUrlsOnlyPageStoredListener testUrlsOnlyPageStoredListener = new TestUrlsOnlyPageStoredListener();
        applicationContext.pageRepository().addListener(testUrlsOnlyPageStoredListener);

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
        assertThat(testUrlsOnlyPageStoredListener.crawledUrls()).containsExactlyInAnyOrderElementsOf(expectedCrawledUrls);
        assertThat(testUrlsOnlyPageStoredListener.foundUrls()).containsExactlyInAnyOrderElementsOf(expectedDiscoveredUrls);
    }

    @Test
    void throwsExceptionIfStartUrlIsInvalid() {
        // given
        DependencyOverrides dependencyOverrides = DependencyOverrides.none();
        CrawlConfig crawlConfig = new CrawlConfig("foo:bar", 1, 10);
        dependencyOverrides.override(new TestFixedCrawlConfigProvider(crawlConfig));
        ApplicationContext applicationContext = new ApplicationContext(dependencyOverrides);
        WebCrawlerApplication webCrawlerApplication = new WebCrawlerApplication(applicationContext);

        // expect
        assertThatIllegalArgumentException().isThrownBy(webCrawlerApplication::start).withMessage("Invalid URL provided.");
    }

    private static String wiremockUrl() {
        return "http://"
                + wireMock.getHost()
                + ":"
                + wireMock.getMappedPort(8080);
    }

}
