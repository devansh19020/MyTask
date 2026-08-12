package com.dev.MyTask.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.Role;
import com.dev.MyTask.exception.ConflictException;
import com.dev.MyTask.exception.GlobalExceptionHandler;
import com.dev.MyTask.exception.ResourceNotFoundException;
import com.dev.MyTask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController API Tests")
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper;

    private User sampleAdmin;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        sampleAdmin = new User();
        sampleAdmin.setId(1L);
        sampleAdmin.setEmail("admin@example.com");
        sampleAdmin.setPassword("hashedAdminPassword");
        sampleAdmin.setRole(Role.ADMIN);

        sampleUser = new User();
        sampleUser.setId(2L);
        sampleUser.setEmail("user@example.com");
        sampleUser.setPassword("hashedUserPassword");
        sampleUser.setRole(Role.USER);
    }

    // ==========================================
    // POST /api/admin/users (Create Admin)
    // ==========================================

    @Test
    @DisplayName("POST /api/admin/users - Success (200 OK)")
    void createAdmin_Success() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("newadmin@example.com");
        request.setPassword("StrongAdminPass123!");

        User createdAdmin = new User();
        createdAdmin.setId(3L);
        createdAdmin.setEmail("newadmin@example.com");
        createdAdmin.setPassword("encoded");
        createdAdmin.setRole(Role.ADMIN);

        when(userService.createAdmin(any(UserCreateRequest.class))).thenReturn(createdAdmin);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("newadmin@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).createAdmin(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/admin/users - Conflict when email already exists (409 Conflict)")
    void createAdmin_EmailConflict_ThrowsConflict() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("admin@example.com");
        request.setPassword("StrongAdminPass123!");

        when(userService.createAdmin(any(UserCreateRequest.class)))
                .thenThrow(new ConflictException("Admin email already exists: admin@example.com"));

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Admin email already exists: admin@example.com"))
                .andExpect(jsonPath("$.path").value("/api/admin/users"));

        verify(userService, times(1)).createAdmin(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/admin/users - Validation Error on invalid email (400 Bad Request)")
    void createAdmin_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("not-a-valid-email");
        request.setPassword("StrongAdminPass123!");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/admin/users - Validation Error on password too short (400 Bad Request)")
    void createAdmin_ShortPassword_ReturnsBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("admin@example.com");
        request.setPassword("short");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    // ==========================================
    // GET /api/admin/users (Get All Users)
    // ==========================================

    @Test
    @DisplayName("GET /api/admin/users - Success returns list of users (200 OK)")
    void getAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleAdmin, sampleUser));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user@example.com"))
                .andExpect(jsonPath("$[1].role").value("USER"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/admin/users - Empty List (200 OK)")
    void getAllUsers_EmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    // ==========================================
    // GET /api/admin/users/{id} (Get User by ID)
    // ==========================================

    @Test
    @DisplayName("GET /api/admin/users/{id} - Success (200 OK)")
    void getUserById_Success() throws Exception {
        when(userService.getUserById(2L)).thenReturn(sampleUser);

        mockMvc.perform(get("/api/admin/users/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).getUserById(2L);
    }

    @Test
    @DisplayName("GET /api/admin/users/{id} - User Not Found (404 Not Found)")
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/admin/users/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/api/admin/users/99"));

        verify(userService, times(1)).getUserById(99L);
    }

    // ==========================================
    // DELETE /api/admin/users/{id} (Delete User)
    // ==========================================

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - Success (200 OK)")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(2L);

        mockMvc.perform(delete("/api/admin/users/{id}", 2L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(2L);
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - User Not Found (404 Not Found)")
    void deleteUser_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: 99"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/admin/users/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found with id: 99"));

        verify(userService, times(1)).deleteUser(99L);
    }
}
