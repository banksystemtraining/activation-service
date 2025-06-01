package com.itgirl.notificationservice.controller;

import com.itgirl.notificationservice.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationWebController {

    private final EmailLogService emailLogService;

    @GetMapping("/emails")
    public String showEmails(@RequestParam(required = false) String search,
                             Model model) {
        List<String> logs = (search != null && !search.isEmpty())
                ? emailLogService.searchLogs(search)
                : emailLogService.getSortedLogs();

        model.addAttribute("emails", logs);
        return "emails";
    }
}