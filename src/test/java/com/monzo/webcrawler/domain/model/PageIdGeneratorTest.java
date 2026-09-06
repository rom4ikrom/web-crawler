package com.monzo.webcrawler.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;

class PageIdGeneratorTest {

    private PageIdGenerator underTest;

    @BeforeEach
    void setup() {
        underTest = new PageIdGenerator();
    }

    @Test
    void generatesUUID() {
        assertThatNoException().isThrownBy(() -> UUID.fromString(underTest.nextId()));
    }

}