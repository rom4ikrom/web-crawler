package com.monzo.webcrawler.infrastructure.repository;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InMemoryPageRepositoryTest {

    private InMemoryPageRepository underTest;

    @Mock
    private PageStoredListener pageStoredListener;

    @BeforeEach
    void setup() {
        underTest = new InMemoryPageRepository();
        underTest.addListener(pageStoredListener);
    }

    @Test
    void storesAndNotifiesListeners() {
        // given
        Page page = Page.builder()
                .id("a-page-id")
                .crawledUrl(new NormalisedUrl("http://something.com", "something.com"))
                .sameDomainUrls(List.of())
                .otherDomainUrls(List.of())
                .invalidUrls(List.of())
                .build();

        // when
        underTest.store(page);

        // then
        Optional<Page> maybePage = underTest.maybePage(page.id());
        assertThat(maybePage).hasValue(page);

        // and
        verify(pageStoredListener, times(1)).onPageStored(page);
    }

}