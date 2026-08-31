package com.monzo.webcrawler;

import jdk.jshell.execution.Util;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

    static void main(String[] args) {
        String startingUrl = "https://crawlme.monzo.com";
        String domain = Utils.extractDomain(startingUrl);
        Queue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer(startingUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();

        UrlProvider urlProvider = new UrlProvider();

        while (!queue.isEmpty()) {
            String url = queue.poll();
            visited.add(url);

            System.out.println("Visiting " + url);

            for (String newUrl : urlProvider.urls(url, domain, visited)) {
                System.out.println("Found " + newUrl);
                queue.offer(newUrl);
            }
        }

    }



}
