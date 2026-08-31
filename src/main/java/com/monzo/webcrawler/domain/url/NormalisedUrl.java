package com.monzo.webcrawler.domain.url;

import lombok.NonNull;
import lombok.Value;

@Value
public class NormalisedUrl {

    @NonNull
    String value;
    @NonNull
    String domain;

}
