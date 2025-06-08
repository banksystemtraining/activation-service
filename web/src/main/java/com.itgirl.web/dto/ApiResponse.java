package com.itgirl.web.dto;

import java.time.Instant;

public record ApiResponse(
        String message,
        int status,
        Instant timestamp
) {
}