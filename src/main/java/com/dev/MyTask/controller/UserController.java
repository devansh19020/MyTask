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

import com.dev.MyTask.entity.Task;
import com.dev.MyTask.entity.User;
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
    public User updateProfile(
            @PathVariable Long userId,
            @RequestBody User updatedUser) {
        return userService.updateUser(userId, updatedUser);
    }

    @DeleteMapping("/profile/{userId}")
    public void deleteAccount(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    // ================= TASKS =================

    @PostMapping("/tasks/{userId}")
    public Task createTask(
            @PathVariable Long userId,
            @RequestBody Task task) {
        return taskService.createTask(userId, task);
    }

    @PutMapping("/tasks/{taskId}")
    public Task updateTask(
            @PathVariable Long taskId,
            @RequestBody Task task) {
        return taskService.updateTask(taskId, task);
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PatchMapping("/tasks/{taskId}/status")
    public Task changeStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {
        return taskService.changeTaskStatus(taskId, status);
    }

    @GetMapping("/tasks/{userId}")
    public Page<Task> getAllTasks(
            @PathVariable Long userId,
            Pageable pageable) {
        return taskService.getAllTasks(userId, pageable);
    }

    @GetMapping("/tasks/{userId}/pending")
    public Page<Task> getPendingTasks(
            @PathVariable Long userId,
            Pageable pageable) {
        return taskService.getPendingTasks(userId, pageable);
    }
}
