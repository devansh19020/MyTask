package com.dev.MyTask.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.MyTask.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    @Override
    boolean existsById(Long id);
    Optional<User> findByEmail(String email);
}

