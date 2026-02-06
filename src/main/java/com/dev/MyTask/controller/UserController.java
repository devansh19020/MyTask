package com.dev.MyTask.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.MyTask.dto.task.TaskCreateRequest;
import com.dev.MyTask.dto.task.TaskResponse;
import com.dev.MyTask.dto.task.TaskUpdateRequest;
import com.dev.MyTask.dto.user.UserResponse;
import com.dev.MyTask.dto.user.UserUpdateRequest;
import com.dev.MyTask.enums.TaskStatus;
import com.dev.MyTask.service.TaskService;
import com.dev.MyTask.service.UserService;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService,
                          TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    // ================= USER PROFILE =================

    @PutMapping("/profile/{userId}")
    public UserResponse updateProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest request) {

        var user = userService.updateUser(userId, request);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @DeleteMapping("/profile/{userId}")
    public void deleteAccount(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    // ================= TASKS =================

    @PostMapping("/tasks/{userId}")
    public TaskResponse createTask(
            @PathVariable Long userId,
            @RequestBody TaskCreateRequest request) {

        var task = taskService.createTask(userId, request);

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }

    @PutMapping("/tasks/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request) {

        var task = taskService.updateTask(taskId, request);

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PatchMapping("/tasks/{taskId}/status")
    public TaskResponse changeStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {

        var task = taskService.changeTaskStatus(taskId, status);

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }

    @GetMapping("/tasks/{userId}")
    public Page<TaskResponse> getAllTasks(
            @PathVariable Long userId,
            Pageable pageable) {

        return taskService.getAllTasks(userId, pageable)
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getDueDate()
                ));
    }

    @GetMapping("/tasks/{userId}/pending")
    public Page<TaskResponse> getPendingTasks(
            @PathVariable Long userId,
            Pageable pageable) {

        return taskService.getPendingTasks(userId, pageable)
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getDueDate()
                ));
    }
}
