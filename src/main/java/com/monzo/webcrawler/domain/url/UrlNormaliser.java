package com.monzo.webcrawler.domain.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

public class UrlNormaliser {

    private static final Set<String> ACCEPTED_SCHEMES = Set.of("http", "https");

    public NormalisedUrl normalise(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Provided string must not be null, empty or whitespace.");
        }
        URI uri = asURI(value);

        String scheme = uri.getScheme();
        if (scheme == null || !ACCEPTED_SCHEMES.contains(scheme)) {
            throw new IllegalArgumentException("Provided URI %s has not acceptable scheme.".formatted(value));
        }
        String host = uri.getHost();
        if (host == null) {
            throw new IllegalArgumentException("URI must contain valid host.");
        }
        URL url = asURL(uri);
        String normalised = url.getProtocol().toLowerCase() + "://" + host.toLowerCase() + url.getPath() + queryOrEmpty(url);
        return new NormalisedUrl(normalised, url.getHost());
    }

    private URI asURI(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URL asURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String queryOrEmpty(URL url) {
        return url.getQuery() != null ? "?" + url.getQuery() : "";
    }


}
