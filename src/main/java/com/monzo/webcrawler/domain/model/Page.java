package com.monzo.webcrawler.domain.model;

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
    String url;
    @NonNull
    List<String> urls;

}
