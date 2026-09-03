package com.monzo.webcrawler.infrastructure.listener;

import com.monzo.webcrawler.domain.listener.PageStoredListener;
import com.monzo.webcrawler.domain.model.Page;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleOutputPageStoredConsumer implements PageStoredListener {

    private final BlockingQueue<Page> queue;
    private final ExecutorService executorService;

    public ConsoleOutputPageStoredConsumer() {
        this(new LinkedBlockingQueue<>(), Executors.newSingleThreadExecutor());
    }

    @Override
    public void onPageStored(Page page) {
        queue.offer(page);
    }

    public void start() {
        executorService.submit(this::consumeLoop);
    }

    private void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Page page = queue.take();
                List<String> urls = page.urls();
                System.out.printf("Discovered %s URLs for: %s%n", urls.size(), page.url());
                for (int i = 1; i <= urls.size(); i++) {
                    System.out.println(i + ". " + urls.get(i - 1));
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }

}
