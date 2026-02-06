package com.dev.MyTask.dto.user;

public class AdminUserResponse {

    private final Long id;
    private final String email;
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