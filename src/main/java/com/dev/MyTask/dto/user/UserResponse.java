package com.dev.MyTask.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User response payload")
public class UserResponse {

    @Schema(description = "Unique user identifier", example = "1")
    private final Long id;

    @Schema(description = "User email address", example = "user@example.com")
    private final String email;

    @Schema(description = "Assigned user role", example = "USER")
    private final String role;

    public UserResponse(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getRole() {
        return role;
    }
}
