package com.monzo.webcrawler.domain.url;

import com.monzo.webcrawler.domain.url.UrlNormalisationResult.InvalidUrl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import static com.monzo.webcrawler.domain.url.UrlNormalisationResult.NormalisedUrl;

public class UrlNormaliser {

    private static final Set<String> ACCEPTED_SCHEMES = Set.of("http", "https");

    public NormalisedUrl normalisedUrlOrThrow(String value) {
        return switch (normalise(value)) {
            case NormalisedUrl normalisedUrl -> normalisedUrl;
            default -> throw new IllegalArgumentException("Invalid URL provided.");
        };
    }

    public UrlNormalisationResult normalise(String value) {
        if (value == null || value.isBlank()) {
            return new InvalidUrl(value);
        }

        try {
            URI uri = new URI(value).normalize();
            String scheme = uri.getScheme();
            if (scheme == null || !ACCEPTED_SCHEMES.contains(scheme.toLowerCase())) {
                return new InvalidUrl(value);
            }
            String host = uri.getHost();
            if (host == null) {
                return new InvalidUrl(value);
            }
            URL url = uri.toURL();
            String normalised = url.getProtocol().toLowerCase() + "://" + host.toLowerCase() + portOrEmpty(url)
                    + url.getPath() + queryOrEmpty(url);
            return new NormalisedUrl(normalised, url.getHost().toLowerCase());
        } catch (URISyntaxException | MalformedURLException ex) {
            return new InvalidUrl(value);
        }
    }

    private String portOrEmpty(URL url) {
        return url.getPort() == -1 ? "" : ":" + url.getPort();
    }

    private String queryOrEmpty(URL url) {
        return url.getQuery() != null ? "?" + url.getQuery() : "";
    }

}
