package com.dev.MyTask.service.implementation;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.dto.user.UserUpdateRequest;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.Role;
import com.dev.MyTask.exception.ConflictException;
import com.dev.MyTask.exception.ResourceNotFoundException;
import com.dev.MyTask.repository.UserRepository;
import com.dev.MyTask.service.UserService;
import com.dev.MyTask.repository.TaskRepository;


@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TaskRepository taskRepository
                        ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRepository = taskRepository;
    }

    // ================= CREATE USER =================

    @Override
    public User createUser(UserCreateRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                "Email already exists: " + request.getEmail()
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }


    // ================= CREATE ADMIN =================

    @Override
    public User createAdmin(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                "Admin email already exists: " + request.getEmail()
            );
        }
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

        if (request.getEmail() != null && request.getEmail() != existingUser.getEmail()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException(
                    "Email already exists: " + request.getEmail()
                );
            }
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

    @Transactional
    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        ));

        taskRepository.deleteByUserId(userId);
        userRepository.delete(user);
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
