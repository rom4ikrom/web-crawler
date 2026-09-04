package com.monzo.webcrawler.domain.url;

import java.util.List;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.*;

public interface UrlProcessor {

    List<NormalisedUrl> process(NormalisedUrl url);

}
