package com.dev.MyTask.controller;

import org.springdoc.core.annotations.ParameterObject;
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

import com.dev.MyTask.dto.error.ErrorResponse;
import com.dev.MyTask.dto.task.TaskCreateRequest;
import com.dev.MyTask.dto.task.TaskResponse;
import com.dev.MyTask.dto.task.TaskUpdateRequest;
import com.dev.MyTask.dto.user.UserResponse;
import com.dev.MyTask.dto.user.UserUpdateRequest;
import com.dev.MyTask.enums.TaskStatus;
import com.dev.MyTask.service.TaskService;
import com.dev.MyTask.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@Tag(name = "User Operations", description = "Endpoints for user profile and task management (Requires USER or ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(
            summary = "Update user profile",
            description = "Updates profile information (such as email or password) for the specified user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserResponse updateProfile(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
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
    @Operation(
            summary = "Delete user account",
            description = "Permanently deletes the user profile and all associated tasks."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User account successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public void deleteAccount(
            @Parameter(description = "ID of the user account to delete", required = true, example = "1")
            @PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    // ================= TASKS =================

    @PostMapping("/tasks/{userId}")
    @Operation(
            summary = "Create task for user",
            description = "Creates a new task associated with the specified user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task successfully created",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error in task request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskResponse createTask(
            @Parameter(description = "ID of the user to create a task for", required = true, example = "1")
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
    @Operation(
            summary = "Update task details",
            description = "Updates the title, description, status, or due date of an existing task."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task successfully updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskResponse updateTask(
            @Parameter(description = "ID of the task to update", required = true, example = "1")
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
    @Operation(
            summary = "Delete task",
            description = "Permanently deletes a task by its task ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public void deleteTask(
            @Parameter(description = "ID of the task to delete", required = true, example = "1")
            @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PatchMapping("/tasks/{taskId}/status")
    @Operation(
            summary = "Change task status",
            description = "Updates the status of a specific task."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status successfully updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status parameter",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskResponse changeStatus(
            @Parameter(description = "ID of the task to update status", required = true, example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "New lifecycle status for the task", required = true)
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
    @Operation(
            summary = "Get all tasks for a user (paginated)",
            description = "Retrieves a paginated list of all tasks assigned to the specified user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public Page<TaskResponse> getAllTasks(
            @Parameter(description = "ID of the user whose tasks to retrieve", required = true, example = "1")
            @PathVariable Long userId,
            @ParameterObject Pageable pageable) {

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
    @Operation(
            summary = "Get pending tasks for a user (paginated)",
            description = "Retrieves a paginated list of tasks with PENDING status for the specified user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pending tasks retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public Page<TaskResponse> getPendingTasks(
            @Parameter(description = "ID of the user whose pending tasks to retrieve", required = true, example = "1")
            @PathVariable Long userId,
            @ParameterObject Pageable pageable) {

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
