package com.dev.MyTask.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Email
    private String email;

    private String password; 

}
