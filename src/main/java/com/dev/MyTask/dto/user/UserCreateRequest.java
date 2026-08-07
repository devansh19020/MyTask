package com.dev.MyTask.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User registration payload")
public class UserCreateRequest {

    @NotBlank
    @Email
    @Schema(description = "Valid email address", example = "newuser@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Size(min = 8)
    @Schema(description = "Password (minimum 8 characters)", example = "SecurePass123!", minLength = 8, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

