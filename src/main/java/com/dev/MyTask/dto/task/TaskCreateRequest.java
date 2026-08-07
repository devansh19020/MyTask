package com.dev.MyTask.dto.task;

import com.dev.MyTask.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Payload for creating a new task")
public class TaskCreateRequest {

    @NotBlank
    @Schema(description = "Task title", example = "Implement Swagger UI", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Detailed task description", example = "Integrate Springdoc OpenAPI for interactive API documentation")
    private String description;

    @NotNull
    @Schema(description = "Initial task status", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    private TaskStatus status;

    @Schema(description = "Due date in YYYY-MM-DD or date string format", example = "2026-12-31")
    private String dueDate;

}
