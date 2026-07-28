package com.enviro.assessment.junior.caitlinnaidu.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consistent shape for every error the API returns, so the frontend always
 * knows where to look for a user-facing message.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> messages
) {
    public static ErrorResponse of(int status, String error, List<String> messages) {
        return new ErrorResponse(LocalDateTime.now(), status, error, messages);
    }
}
