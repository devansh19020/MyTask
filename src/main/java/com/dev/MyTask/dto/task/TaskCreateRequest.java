package com.dev.MyTask.dto.task;

import com.dev.MyTask.enums.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TaskStatus status;

    private String dueDate;

}
