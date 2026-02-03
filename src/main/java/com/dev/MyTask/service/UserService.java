package com.dev.MyTask.service;

import java.util.List;

import com.dev.MyTask.entity.User;

public interface UserService {

    // USER functions
    User createUser(User user);
    User updateUser(Long userId, User updatedUser);
    void deleteUser(Long userId);

    // ADMIN functions
    User createAdmin(User user);
    List<User> getAllUsers();
    User getUserById(Long userId);
}

