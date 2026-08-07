package com.dev.MyTask.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "User login credentials payload")
public class LoginRequest {

    @NotBlank
    @Schema(description = "Registered email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "Account password", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
