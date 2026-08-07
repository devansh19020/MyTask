package com.dev.MyTask.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Schema(description = "User profile update payload")
public class UserUpdateRequest {

    @Email
    @Schema(description = "Updated email address", example = "updateduser@example.com")
    private String email;

    @Schema(description = "Updated password (optional)", example = "NewSecret123!")
    private String password; 

}
