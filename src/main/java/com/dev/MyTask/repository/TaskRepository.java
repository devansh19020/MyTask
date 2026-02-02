package com.dev.MyTask.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.MyTask.entity.Task;
import com.dev.MyTask.enums.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);

    Page<Task> findByUserIdAndStatus(
            Long userId,
            TaskStatus status,
            Pageable pageable
    );
}


