package com.dev.MyTask.entity;

import com.dev.MyTask.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Schema(description = "Task entity representation")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique task identifier", example = "1")
    private Long id;

    @NonNull
    @Schema(description = "Task title", example = "Implement API documentation")
    private String title;
    @NonNull
    @Schema(description = "Task description", example = "Integrate Swagger UI with OpenAPI")
    private String description;
    @NonNull
    @Schema(description = "Task status", example = "PENDING")
    private TaskStatus status;
    @NonNull
    @Schema(description = "Task due date", example = "2026-12-31")
    private String dueDate;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "User who owns this task")
    private User user;
}
