package com.monzo.webcrawler.util;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryAppender extends AbstractAppender {

    private final List<LogEvent> events;

    public InMemoryAppender() {
        super("in-memory-appender", null, null, true, new Property[]{});
        this.events = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void append(LogEvent event) {
        events.add(event.toImmutable());
    }

    public int size() {
        return events.size();
    }

    public List<String> eventMessages() {
        return events.stream()
                .map(LogEvent::getMessage)
                .map(Message::getFormattedMessage)
                .collect(Collectors.toList());
    }

    public void clear() {
        events.clear();
    }
}
