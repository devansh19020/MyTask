package com.dev.MyTask.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin view of user details response payload")
public class AdminUserResponse {

    @Schema(description = "Unique user identifier", example = "1")
    private final Long id;
    @Schema(description = "User email address", example = "admin@example.com")
    private final String email;
    @Schema(description = "Assigned user role", example = "ADMIN")
    private final String role;

    public AdminUserResponse(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // getters
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