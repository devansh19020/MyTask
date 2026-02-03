package com.dev.MyTask.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dev.MyTask.entity.Task;
import com.dev.MyTask.enums.TaskStatus;

public interface TaskService {

    Task createTask(Long userId, Task task);
    Task updateTask(Long taskId, Task updatedTask);
    void deleteTask(Long taskId);
    Task changeTaskStatus(Long taskId, TaskStatus status);

    Page<Task> getAllTasks(Long userId, Pageable pageable);
    Page<Task> getPendingTasks(Long userId, Pageable pageable);
}

