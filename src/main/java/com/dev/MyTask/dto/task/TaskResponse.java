package com.dev.MyTask.dto.task;

import com.dev.MyTask.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task details response payload")
public class TaskResponse {

    @Schema(description = "Unique task identifier", example = "1")
    private final Long id;

    @Schema(description = "Task title", example = "Implement Swagger UI")
    private final String title;

    @Schema(description = "Detailed task description", example = "Integrate Springdoc OpenAPI for interactive API documentation")
    private final String description;

    @Schema(description = "Current status of the task", example = "PENDING")
    private final TaskStatus status;

    @Schema(description = "Task due date", example = "2026-12-31")
    private final String dueDate;

    public TaskResponse(Long id, String title,
                        String description,
                        TaskStatus status,
                        String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    // getters
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public TaskStatus getStatus() {
        return status;
    }
    public String getDueDate() {
        return dueDate;
    }
}