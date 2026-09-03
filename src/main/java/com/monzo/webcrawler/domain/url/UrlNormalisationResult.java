package com.monzo.webcrawler.domain.url;

import lombok.NonNull;

public sealed interface UrlNormalisationResult {

    record NormalisedUrl(@NonNull String value,
                         @NonNull String domain) implements UrlNormalisationResult {

        public boolean hasSameDomainAs(String other) {
            return this.domain.equals(other);
        }

    }

    final class InvalidUrl implements UrlNormalisationResult {

        private static final InvalidUrl INVALID_URL = new InvalidUrl();

        private InvalidUrl() {}

        public static InvalidUrl instance() {
            return INVALID_URL;
        }
    }


}
