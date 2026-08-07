package com.dev.MyTask.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User authorization role")
public enum Role {
    ADMIN,
    USER
}
