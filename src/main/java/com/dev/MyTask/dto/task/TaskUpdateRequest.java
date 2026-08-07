package com.dev.MyTask.dto.task;

import com.dev.MyTask.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Payload for updating an existing task")
public class TaskUpdateRequest {

    @Schema(description = "Updated task title", example = "Updated Swagger Documentation")
    private String title;

    @Schema(description = "Updated task description", example = "Updated Springdoc OpenAPI annotations and configuration")
    private String description;

    @Schema(description = "Updated task status", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Updated due date in YYYY-MM-DD or date string format", example = "2026-12-31")
    private String dueDate;

}
