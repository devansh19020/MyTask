package com.dev.MyTask.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.MyTask.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
