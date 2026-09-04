package com.monzo.webcrawler.domain.url;

import lombok.NonNull;

public sealed interface UrlNormalisationResult {

    boolean isNormalised();

    record NormalisedUrl(@NonNull String value,
                         @NonNull String domain) implements UrlNormalisationResult {

        public boolean hasSameDomainAs(String other) {
            return this.domain.equals(other);
        }

        @Override
        public boolean isNormalised() {
            return true;
        }
    }

    final class InvalidUrl implements UrlNormalisationResult {

        private static final InvalidUrl INVALID_URL = new InvalidUrl();

        private InvalidUrl() {}

        public static InvalidUrl instance() {
            return INVALID_URL;
        }

        @Override
        public boolean isNormalised() {
            return false;
        }
    }


}
