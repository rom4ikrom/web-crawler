package com.monzo.webcrawler.domain.model;

import com.monzo.webcrawler.domain.url.UrlNormalisationResult.InvalidUrl;
import com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Page {

    @NonNull
    String id;
    @NonNull
    NormalisedUrl crawledUrl;
    @NonNull
    List<NormalisedUrl> sameDomainUrls;
    @NonNull
    List<NormalisedUrl> otherDomainUrls;
    @NonNull
    List<InvalidUrl> invalidUrls;

}
