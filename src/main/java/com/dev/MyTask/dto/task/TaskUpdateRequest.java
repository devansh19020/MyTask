package com.dev.MyTask.dto.task;

import com.dev.MyTask.enums.TaskStatus;

import lombok.Data;

@Data
public class TaskUpdateRequest {

    private String title;
    private String description;
    private TaskStatus status;
    private String dueDate;

}
