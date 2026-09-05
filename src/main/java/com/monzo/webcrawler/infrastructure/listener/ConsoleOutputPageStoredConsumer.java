package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleOutputPageStoredConsumer implements PageStoredListener {

    private static final Logger LOG = LogManager.getLogger();

    private static final Page POISON_PILL = Page.builder().id("poison-pill").url("poison-pill").urls(List.of()).build();

    private final BlockingQueue<Page> queue;
    private Thread thread;

    public ConsoleOutputPageStoredConsumer() {
        this(new LinkedBlockingQueue<>());
    }

    @Override
    public void onPageStored(Page page) {
        queue.offer(page);
    }

    public void start() {
        thread = Thread.ofVirtual()
                .name("console-consumer")
                .start(this::consumeLoop);
    }

    private void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Page page = queue.take();

                if (page == POISON_PILL) {
                    LOG.info("Poison pill, stopping console thead...");
                    return;
                }

                List<String> urls = page.urls();
                LOG.info("Discovered {} URLs for: {}", urls.size(), page.url());
                for (int i = 1; i <= urls.size(); i++) {
                    LOG.info("{}. {}", i, urls.get(i - 1));
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void completePublishing() {
        queue.add(POISON_PILL);
    }

    @Override
    public void close() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void awaitCompletion() throws InterruptedException {
        thread.join();
    }

}
