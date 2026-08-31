package com.monzo.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestResources {

    public static String read(String resourcePath) {
        try (InputStream inputStream = TestResources.class
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Resource not found: " + resourcePath);
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read resource: " + resourcePath, e);
        }
    }


}
