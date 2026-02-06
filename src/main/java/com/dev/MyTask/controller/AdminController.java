package com.dev.MyTask.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.MyTask.dto.user.AdminUserResponse;
import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.dto.user.UserResponse;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // ================= USERS =================

    @PostMapping("/users")
    public UserResponse createAdmin(
            @Valid @RequestBody UserCreateRequest request) {

        User admin = userService.createAdmin(request);
        return new UserResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getRole().name()
        );
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public AdminUserResponse getUserById(@PathVariable Long id) {

        User user = userService.getUserById(id);

        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

