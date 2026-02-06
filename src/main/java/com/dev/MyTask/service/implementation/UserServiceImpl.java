package com.dev.MyTask.service.implementation;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.dto.user.UserUpdateRequest;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.Role;
import com.dev.MyTask.exception.ResourceNotFoundException;
import com.dev.MyTask.repository.UserRepository;
import com.dev.MyTask.service.UserService;


@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= CREATE USER =================

    @Override
    public User createUser(UserCreateRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    // ================= CREATE ADMIN =================

    @Override
    public User createAdmin(UserCreateRequest request) {

        User admin = new User();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);

        return userRepository.save(admin);
    }

    // ================= UPDATE USER =================

    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {

        User existingUser = getUserById(userId);

        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }

        if (request.getPassword() != null &&
            !request.getPassword().isBlank()) {

            existingUser.setPassword(
                passwordEncoder.encode(request.getPassword())
            );
        }

        return userRepository.save(existingUser);
    }

    // ================= DELETE =================

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // ================= FETCH =================

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        ));
    }
}
