package com.itgirl.notificationservice.controller;

import com.itgirl.notificationservice.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebController {

    private final EmailLogService emailLogService;
    private static final int PAGE_SIZE = 10;

    @GetMapping("/emails")
    public String showEmails(@RequestParam(required = false) String search,
                             @RequestParam(defaultValue = "1") int page,
                             Model model) {
        log.info("Received request to /emails with search='{}', page={}", search, page);

        List<String> logs;
        if (search == null || search.isBlank()) {
            logs = emailLogService.getSortedLogs();
        } else {
            logs = emailLogService.searchLogs(search);
            model.addAttribute("search", search);
        }

        int totalLogs = logs.size();
        int totalPages = totalLogs > 0 ? (int) Math.ceil((double) totalLogs / PAGE_SIZE) : 1;

        page = Math.max(1, Math.min(page, totalPages));

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalLogs);

        List<String> pageLogs = (totalLogs > 0 && start < end)
                ? logs.subList(start, end)
                : Collections.emptyList();

        if (pageLogs.isEmpty()) {
            model.addAttribute("message", "No emails found for your query.");
        }

        model.addAttribute("emails", pageLogs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);

        return "emails";
    }
}