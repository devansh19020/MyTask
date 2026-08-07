package com.dev.MyTask.dto.error;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standardized error response payload")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private final int status;

    @Schema(description = "Error code or category", example = "NOT_FOUND")
    private final String error;

    @Schema(description = "Detailed error message", example = "Resource not found")
    private final String message;

    @Schema(description = "Target request URI path", example = "/api/user/tasks/1")
    private final String path;

    @Schema(description = "Timestamp when error occurred", example = "2026-10-13T14:25:31")
    private final LocalDateTime timestamp;

    public ErrorResponse(
            int status,
            String error,
            String message,
            String path
    ) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
