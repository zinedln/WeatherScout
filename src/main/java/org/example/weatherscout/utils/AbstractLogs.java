package org.example.weatherscout.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractLogs {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH::mm:ss");

    protected void log(String message) {
        String time = LocalDateTime.now().format(TIME_FORMAT);
        System.out.println("[" + time + "] [" + getPrefix() + "] " + message);
    }

    protected abstract String getPrefix();
}
