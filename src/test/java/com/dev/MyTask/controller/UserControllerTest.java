package com.dev.MyTask.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dev.MyTask.dto.task.TaskCreateRequest;
import com.dev.MyTask.dto.task.TaskUpdateRequest;
import com.dev.MyTask.dto.user.UserUpdateRequest;
import com.dev.MyTask.entity.Task;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.Role;
import com.dev.MyTask.enums.TaskStatus;
import com.dev.MyTask.exception.ConflictException;
import com.dev.MyTask.exception.GlobalExceptionHandler;
import com.dev.MyTask.exception.ResourceNotFoundException;
import com.dev.MyTask.service.TaskService;
import com.dev.MyTask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController API Tests")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private User sampleUser;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentMethodResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@example.com");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setRole(Role.USER);

        sampleTask = new Task();
        sampleTask.setId(10L);
        sampleTask.setTitle("API Testing Task");
        sampleTask.setDescription("Write Mockito and JUnit tests");
        sampleTask.setStatus(TaskStatus.PENDING);
        sampleTask.setDueDate("2026-12-31");
        sampleTask.setUser(sampleUser);
    }

    // ==========================================
    // PUT /api/user/profile/{userId}
    // ==========================================

    @Test
    @DisplayName("PUT /api/user/profile/{userId} - Success (200 OK)")
    void updateProfile_Success() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("updated@example.com");
        request.setPassword("NewStrongPass123!");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newEncodedPassword");
        updatedUser.setRole(Role.USER);

        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/user/profile/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /api/user/profile/{userId} - User Not Found (404 Not Found)")
    void updateProfile_UserNotFound() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("updated@example.com");

        when(userService.updateUser(eq(99L), any(UserUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(put("/api/user/profile/{userId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/api/user/profile/99"));
    }

    @Test
    @DisplayName("PUT /api/user/profile/{userId} - Email Conflict (409 Conflict)")
    void updateProfile_EmailConflict() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("taken@example.com");

        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
                .thenThrow(new ConflictException("Email already exists: taken@example.com"));

        mockMvc.perform(put("/api/user/profile/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Email already exists: taken@example.com"));
    }

    // ==========================================
    // DELETE /api/user/profile/{userId}
    // ==========================================

    @Test
    @DisplayName("DELETE /api/user/profile/{userId} - Success (200 OK)")
    void deleteAccount_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/user/profile/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/user/profile/{userId} - User Not Found (404 Not Found)")
    void deleteAccount_UserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: 99"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/user/profile/{userId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found with id: 99"));
    }

    // ==========================================
    // POST /api/user/tasks/{userId} (Create Task)
    // ==========================================

    @Test
    @DisplayName("POST /api/user/tasks/{userId} - Success (200 OK)")
    void createTask_Success() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("API Testing Task");
        request.setDescription("Write Mockito and JUnit tests");
        request.setStatus(TaskStatus.PENDING);
        request.setDueDate("2026-12-31");

        when(taskService.createTask(eq(1L), any(TaskCreateRequest.class))).thenReturn(sampleTask);

        mockMvc.perform(post("/api/user/tasks/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("API Testing Task"))
                .andExpect(jsonPath("$.description").value("Write Mockito and JUnit tests"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.dueDate").value("2026-12-31"));

        verify(taskService, times(1)).createTask(eq(1L), any(TaskCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/user/tasks/{userId} - User Not Found (404 Not Found)")
    void createTask_UserNotFound() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("API Testing Task");
        request.setDescription("Write Mockito and JUnit tests");
        request.setStatus(TaskStatus.PENDING);
        request.setDueDate("2026-12-31");

        when(taskService.createTask(eq(99L), any(TaskCreateRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(post("/api/user/tasks/{userId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    // ==========================================
    // PUT /api/user/tasks/{taskId} (Update Task)
    // ==========================================

    @Test
    @DisplayName("PUT /api/user/tasks/{taskId} - Success (200 OK)")
    void updateTask_Success() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Task Title");
        request.setDescription("Updated Task Description");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setDueDate("2027-01-15");

        Task updatedTask = new Task();
        updatedTask.setId(10L);
        updatedTask.setTitle("Updated Task Title");
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setDueDate("2027-01-15");
        updatedTask.setUser(sampleUser);

        when(taskService.updateTask(eq(10L), any(TaskUpdateRequest.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/user/tasks/{taskId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Updated Task Title"))
                .andExpect(jsonPath("$.description").value("Updated Task Description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.dueDate").value("2027-01-15"));

        verify(taskService, times(1)).updateTask(eq(10L), any(TaskUpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /api/user/tasks/{taskId} - Task Not Found (404 Not Found)")
    void updateTask_TaskNotFound() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Task Title");

        when(taskService.updateTask(eq(999L), any(TaskUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        mockMvc.perform(put("/api/user/tasks/{taskId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    // ==========================================
    // DELETE /api/user/tasks/{taskId}
    // ==========================================

    @Test
    @DisplayName("DELETE /api/user/tasks/{taskId} - Success (200 OK)")
    void deleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(10L);

        mockMvc.perform(delete("/api/user/tasks/{taskId}", 10L))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(10L);
    }

    @Test
    @DisplayName("DELETE /api/user/tasks/{taskId} - Task Not Found (404 Not Found)")
    void deleteTask_TaskNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found with id: 999"))
                .when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/api/user/tasks/{taskId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    // ==========================================
    // PATCH /api/user/tasks/{taskId}/status
    // ==========================================

    @Test
    @DisplayName("PATCH /api/user/tasks/{taskId}/status - Success (200 OK)")
    void changeStatus_Success() throws Exception {
        Task completedTask = new Task();
        completedTask.setId(10L);
        completedTask.setTitle("API Testing Task");
        completedTask.setDescription("Write Mockito and JUnit tests");
        completedTask.setStatus(TaskStatus.COMPLETED);
        completedTask.setDueDate("2026-12-31");
        completedTask.setUser(sampleUser);

        when(taskService.changeTaskStatus(10L, TaskStatus.COMPLETED)).thenReturn(completedTask);

        mockMvc.perform(patch("/api/user/tasks/{taskId}/status", 10L)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(taskService, times(1)).changeTaskStatus(10L, TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("PATCH /api/user/tasks/{taskId}/status - Invalid Transition Bad Request (400 Bad Request)")
    void changeStatus_InvalidTransition_ReturnsBadRequest() throws Exception {
        when(taskService.changeTaskStatus(10L, TaskStatus.PENDING))
                .thenThrow(new IllegalArgumentException("Completed task cannot be moved back to pending"));

        mockMvc.perform(patch("/api/user/tasks/{taskId}/status", 10L)
                        .param("status", "PENDING"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Completed task cannot be moved back to pending"));
    }

    @Test
    @DisplayName("PATCH /api/user/tasks/{taskId}/status - Task Not Found (404 Not Found)")
    void changeStatus_TaskNotFound() throws Exception {
        when(taskService.changeTaskStatus(999L, TaskStatus.COMPLETED))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        mockMvc.perform(patch("/api/user/tasks/{taskId}/status", 999L)
                        .param("status", "COMPLETED"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    // ==========================================
    // GET /api/user/tasks/{userId} (Paginated)
    // ==========================================

    @Test
    @DisplayName("GET /api/user/tasks/{userId} - Success (200 OK)")
    void getAllTasks_Success() throws Exception {
        Task task2 = new Task();
        task2.setId(11L);
        task2.setTitle("Second Task");
        task2.setDescription("Second Desc");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setDueDate("2026-11-30");
        task2.setUser(sampleUser);

        Page<Task> taskPage = new PageImpl<>(List.of(sampleTask, task2), PageRequest.of(0, 10), 2);

        when(taskService.getAllTasks(eq(1L), any(Pageable.class))).thenReturn(taskPage);

        mockMvc.perform(get("/api/user/tasks/{userId}", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].title").value("API Testing Task"))
                .andExpect(jsonPath("$.content[1].id").value(11))
                .andExpect(jsonPath("$.content[1].title").value("Second Task"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(taskService, times(1)).getAllTasks(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/user/tasks/{userId} - User Not Found (404 Not Found)")
    void getAllTasks_UserNotFound() throws Exception {
        when(taskService.getAllTasks(eq(99L), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/user/tasks/{userId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    // ==========================================
    // GET /api/user/tasks/{userId}/pending
    // ==========================================

    @Test
    @DisplayName("GET /api/user/tasks/{userId}/pending - Success (200 OK)")
    void getPendingTasks_Success() throws Exception {
        Page<Task> pendingPage = new PageImpl<>(List.of(sampleTask), PageRequest.of(0, 10), 1);

        when(taskService.getPendingTasks(eq(1L), any(Pageable.class))).thenReturn(pendingPage);

        mockMvc.perform(get("/api/user/tasks/{userId}/pending", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(taskService, times(1)).getPendingTasks(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/user/tasks/{userId}/pending - User Not Found (404 Not Found)")
    void getPendingTasks_UserNotFound() throws Exception {
        when(taskService.getPendingTasks(eq(99L), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/user/tasks/{userId}/pending", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
