package com.dev.MyTask.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.MyTask.config.JwtUtil;
import com.dev.MyTask.dto.auth.LoginRequest;
import com.dev.MyTask.dto.auth.LoginResponse;
import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.dto.user.UserResponse;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public PublicController(UserService userService,
                            AuthenticationManager authenticationManager,
                            JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserCreateRequest request) {

        User user = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name()
                ));
    }

    // Login (JWT)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getEmail());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}

