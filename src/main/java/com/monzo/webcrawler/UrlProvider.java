package com.monzo.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class UrlProvider {

    public Set<String> urls(String target, String domain, Set<String> visited) {
        try {
            Document document = Jsoup.connect(target).timeout(5000).get();

            Set<String> urls = new HashSet<>();
            document.select("a[href]").forEach(link -> {
                String href = link.attr("abs:href");
                try {
                    URI uri = new URI(href);
                    String scheme = uri.getScheme();
                    if (!"http".equals(scheme) && !"https".equals(scheme)) {
                        return;
                    }

                    URL url = uri.toURL();

                    String normalised = url.getProtocol().toLowerCase() + "://"
                            + url.getHost().toLowerCase()
                            + url.getPath()
                            + (url.getQuery() != null ? "?" + url.getQuery() : "");

                    if (url.getHost().equals(domain) && !visited.contains(normalised)) {
                        urls.add(normalised);
                    }

                } catch (MalformedURLException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });

            return urls;
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

}
