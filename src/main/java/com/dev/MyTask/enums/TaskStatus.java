package com.dev.MyTask.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lifecycle status of a task: PENDING, IN_PROGRESS, COMPLETED")
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
