package com.dev.MyTask.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.dev.MyTask.config.JwtUtil;
import com.dev.MyTask.dto.auth.LoginRequest;
import com.dev.MyTask.dto.user.UserCreateRequest;
import com.dev.MyTask.entity.User;
import com.dev.MyTask.enums.Role;
import com.dev.MyTask.exception.ConflictException;
import com.dev.MyTask.exception.GlobalExceptionHandler;
import com.dev.MyTask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublicController API Tests")
class PublicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PublicController publicController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(publicController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==========================================
    // POST /api/public/signup (User Registration)
    // ==========================================

    @Test
    @DisplayName("POST /api/public/signup - Success (201 Created)")
    void registerUser_Success() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("StrongPassword123!");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setEmail("newuser@example.com");
        createdUser.setPassword("encodedPasswordHash");
        createdUser.setRole(Role.USER);

        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).createUser(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/public/signup - Conflict when email already exists (409 Conflict)")
    void registerUser_EmailAlreadyExists_ThrowsConflict() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("StrongPassword123!");

        when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new ConflictException("Email already exists: duplicate@example.com"));

        mockMvc.perform(post("/api/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Email already exists: duplicate@example.com"))
                .andExpect(jsonPath("$.path").value("/api/public/signup"));

        verify(userService, times(1)).createUser(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/public/signup - Validation Error for invalid email format (400 Bad Request)")
    void registerUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("not-a-valid-email");
        request.setPassword("StrongPassword123!");

        mockMvc.perform(post("/api/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/public/signup - Validation Error when password is less than 8 characters (400 Bad Request)")
    void registerUser_ShortPassword_ReturnsBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("valid@example.com");
        request.setPassword("short");

        mockMvc.perform(post("/api/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/public/signup - Validation Error when email is blank (400 Bad Request)")
    void registerUser_BlankEmail_ReturnsBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("");
        request.setPassword("StrongPassword123!");

        mockMvc.perform(post("/api/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    // ==========================================
    // POST /api/public/login (User Authentication)
    // ==========================================

    @Test
    @DisplayName("POST /api/public/login - Success (200 OK)")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Password123!");

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtUtil.generateToken("user@example.com")).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("user@example.com");
    }

    @Test
    @DisplayName("POST /api/public/login - Bad Credentials (500 Generic error handled)")
    void login_BadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("WrongPassword!");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    @DisplayName("POST /api/public/login - Validation Error for blank email (400 Bad Request)")
    void login_BlankEmail_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("Password123!");

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/public/login - Validation Error for blank password (400 Bad Request)")
    void login_BlankPassword_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("");

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
