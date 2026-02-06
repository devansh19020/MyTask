package com.dev.MyTask.service;

import java.util.List;

import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.dto.user.UserUpdateRequest;
import com.dev.MyTask.entity.User;

public interface UserService {

    User createUser(UserCreateRequest request);
    User createAdmin(UserCreateRequest request);
    User updateUser(Long userId, UserUpdateRequest request);
    void deleteUser(Long userId);
    List<User> getAllUsers();
    User getUserById(Long userId);
}


