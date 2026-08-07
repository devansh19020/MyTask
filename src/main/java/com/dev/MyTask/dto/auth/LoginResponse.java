package com.dev.MyTask.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response payload containing bearer access token")
public class LoginResponse {

    @Schema(description = "Signed JWT bearer access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
