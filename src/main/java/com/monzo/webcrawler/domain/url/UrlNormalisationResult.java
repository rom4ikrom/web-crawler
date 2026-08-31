package com.monzo.webcrawler.domain.url;

import lombok.NonNull;

public sealed interface UrlNormalisationResult {

    boolean valid();

    record NormalisedUrl(@NonNull String value,
                         @NonNull String domain) implements UrlNormalisationResult {

        public boolean hasSameDomainAs(String other) {
            return this.domain.equals(other);
        }

        @Override
        public boolean valid() {
            return true;
        }
    }

    final class InvalidUrl implements UrlNormalisationResult {

        private static final InvalidUrl INVALID_URL = new InvalidUrl();

        private InvalidUrl() {}

        @Override
        public boolean valid() {
            return false;
        }

        public static InvalidUrl instance() {
            return INVALID_URL;
        }
    }


}
