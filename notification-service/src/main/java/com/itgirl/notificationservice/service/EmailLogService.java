package com.itgirl.notificationservice.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailLogService {

    private final Path logFile = Paths.get("emails.log");
    private final List<String> logs = Collections.synchronizedList(new ArrayList<>());

    public synchronized void logEmail(String email, String content) {
        String logEntry = String.format("%s - Email: %s, Content: %s",
                LocalDateTime.now(), email, content);
        logs.add(logEntry);
        try {
            Files.writeString(logFile, logEntry + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write log", e);
        }
    }

    public List<String> getSortedLogs() {
        synchronized (logs) {
            return logs.stream()
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public List<String> searchLogs(String keyword) {
        synchronized (logs) {
            return logs.stream()
                    .filter(line -> line.contains(keyword))
                    .collect(Collectors.toList());
        }
    }
}