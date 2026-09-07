package com.monzo.webcrawler.domain.url;

import lombok.NonNull;

public sealed interface UrlNormalisationResult {

    String value();

    record NormalisedUrl(@NonNull String value,
                         @NonNull String domain) implements UrlNormalisationResult {

        public boolean hasSameDomainAs(String other) {
            return this.domain.equals(other);
        }

    }

    record InvalidUrl(String value) implements UrlNormalisationResult {

        public boolean isNullOrBlank() {
            return value == null || value.isBlank();
        }

    }


}
