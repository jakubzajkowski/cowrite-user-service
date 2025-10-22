package com.example.cowrite;

import com.example.cowrite.dto.RegisterRequest;
import com.example.cowrite.dto.UserDto;
import com.example.cowrite.entity.User;
import com.example.cowrite.repository.UserRepository;
import com.example.cowrite.service.AuthService;
import com.example.cowrite.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceUnitTest {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private HttpServletResponse response;
    private ObjectMapper objectMapper;
    private AuthService authService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        response = mock(HttpServletResponse.class);
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthService(userRepository, jwtUtil, objectMapper);

        ReflectionTestUtils.setField(authService, "COOKIE_NAME", "jwt-token");
    }

    @Test
    void register_shouldSaveUser_whenEmailNotExists() {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "TestUser");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto result = authService.register(request);

        assertEquals("TestUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertNotNull(result.getId());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenEmailExists() {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "TestUser");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    // ---------------- login ----------------
    @Test
    void login_shouldReturnUserDtoAndSetCookie_whenPasswordMatches() throws Exception {
        String rawPassword = "password123";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("TestUser");
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString())).thenReturn("mocked-jwt-token");

        UserDto result = authService.login("test@example.com", rawPassword, response);

        assertEquals("TestUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1L, result.getId());

        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> authService.login("test@example.com", "password123", response));
    }

    @Test
    void login_shouldThrow_whenPasswordInvalid() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("correctPassword"));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.login("test@example.com", "wrongPassword", response));
    }

    // ---------------- getCurrentUser ----------------
    @Test
    void getCurrentUser_shouldReturnUserDto_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("TestUser");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDto result = authService.getCurrentUser("test@example.com");

        assertEquals("TestUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1L, result.getId());
    }

    @Test
    void getCurrentUser_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.getCurrentUser("test@example.com"));
    }
}
