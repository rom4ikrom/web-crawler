package com.monzo.webcrawler.infrastructure.jsoup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupFacade {

    private static final Logger LOG = LogManager.getLogger();
    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    public Document document(String url) {
        try {
            return Jsoup.connect(url).timeout(DEFAULT_TIMEOUT_MILLIS).get();
        } catch (Exception ex) {
            LOG.error("Failed to fetch document due to: ", ex);
            throw new RuntimeException(ex);
        }
    }

}
