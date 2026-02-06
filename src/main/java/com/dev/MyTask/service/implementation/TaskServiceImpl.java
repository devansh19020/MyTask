package com.dev.MyTask.service.implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.MyTask.dto.task.TaskCreateRequest;
import com.dev.MyTask.dto.task.TaskUpdateRequest;
import com.dev.MyTask.entity.Task;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.TaskStatus;
import com.dev.MyTask.exception.ResourceNotFoundException;
import com.dev.MyTask.repository.TaskRepository;
import com.dev.MyTask.repository.UserRepository;
import com.dev.MyTask.service.TaskService;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                           UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // ================= CREATE =================

    @Override
    public Task createTask(Long userId, TaskCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setUser(user);

        return taskRepository.save(task);
    }

    // ================= UPDATE =================

    @Override
    public Task updateTask(Long taskId, TaskUpdateRequest request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + taskId
                        ));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        return taskRepository.save(task);
    }

    // ================= DELETE =================

    @Override
    public void deleteTask(Long taskId) {

        Task task = getTaskById(taskId);
        taskRepository.delete(task);
    }

    // ================= STATUS CHANGE =================

    @Override
    public Task changeTaskStatus(Long taskId, TaskStatus status) {

        Task task = getTaskById(taskId);
        task.setStatus(status);

        return taskRepository.save(task);
    }

    // ================= READ =================

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getAllTasks(Long userId, Pageable pageable) {

        validateUserExists(userId);
        return taskRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getPendingTasks(Long userId, Pageable pageable) {

        validateUserExists(userId);
        return taskRepository.findByUserIdAndStatus(
                userId,
                TaskStatus.TODO,
                pageable
        );
    }

    // ================= PRIVATE HELPERS =================

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + taskId
                        )
                );
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }
    }
}
