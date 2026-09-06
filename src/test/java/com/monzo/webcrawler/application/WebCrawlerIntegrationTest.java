package com.monzo.webcrawler.application;

import com.monzo.webcrawler.bootstrap.ApplicationContext;
import com.monzo.webcrawler.bootstrap.DependencyOverrides;
import com.monzo.webcrawler.bootstrap.TestFixedCrawlConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class WebCrawlerIntegrationTest {

    private ApplicationContext applicationContext;

    @Container
    static final GenericContainer<?> wireMock =
            new GenericContainer<>("wiremock/wiremock:3.13.0")
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
        CrawlConfig crawlConfig = new CrawlConfig(wireMockUrl("/page1"), 1, 7);
        dependencyOverrides.override(new TestFixedCrawlConfigProvider(crawlConfig));
        applicationContext = new ApplicationContext(dependencyOverrides);
    }

    @Test
    void test() {
        new WebCrawlerApplication(applicationContext).start();
    }

    private static String wireMockUrl(String path) {
        return "http://"
                + wireMock.getHost()
                + ":"
                + wireMock.getMappedPort(8080)
                + path;
    }

}
