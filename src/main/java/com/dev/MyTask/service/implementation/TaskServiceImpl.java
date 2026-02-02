package com.dev.MyTask.service.implementation;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.MyTask.entity.Task;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.TaskStatus;
import com.dev.MyTask.repository.UserRepository;
import com.dev.MyTask.service.TaskService;
import com.dev.MyTask.exception.ResourceNotFoundException;
import com.dev.MyTask.repository.TaskRepository;

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
    public Task createTask(Long userId, Task task) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        task.setId(null); // safety: force new entity
        task.setUser(user);
        task.setStatus(TaskStatus.TODO);

        return taskRepository.save(task);
    }

    // ================= UPDATE =================

    @Override
    public Task updateTask(Long taskId, Task updatedTask) {

        Task existingTask = getTaskById(taskId);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());

        return taskRepository.save(existingTask);
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
