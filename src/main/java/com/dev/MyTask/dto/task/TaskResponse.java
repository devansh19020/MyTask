package com.dev.MyTask.dto.task;

import com.dev.MyTask.enums.TaskStatus;

public class TaskResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final TaskStatus status;
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