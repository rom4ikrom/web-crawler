package com.monzo.webcrawler;

import java.net.URI;
import java.net.URISyntaxException;

public class Utils {

    private Utils() {}

    public static String extractDomain(String url) {
        try {
            return new URI(url).getHost();
        } catch (URISyntaxException ex) {
            throw new RuntimeException();
        }
    }

}
