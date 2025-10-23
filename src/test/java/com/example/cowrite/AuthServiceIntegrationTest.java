package com.example.cowrite;

import com.example.cowrite.dto.RegisterRequest;
import com.example.cowrite.dto.UserDto;
import com.example.cowrite.entity.User;
import com.example.cowrite.repository.UserRepository;
import com.example.cowrite.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceIntegrationTest extends AbstractTestContainers {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void testRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest("integration@example.com", "password123", "integrationUser");

        UserDto dto = authService.register(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("integration@example.com");
        assertThat(dto.getUsername()).isEqualTo("integrationUser");

        Optional<User> userOpt = userRepository.findByEmail("integration@example.com");
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getUsername()).isEqualTo("integrationUser");
    }

    @Test
    void testRegisterDuplicateEmailThrows() {
        RegisterRequest request = new RegisterRequest("duplicate@example.com", "pass", "user1");

        authService.register(request);

        RegisterRequest dupRequest = new RegisterRequest("duplicate@example.com", "pass2", "user2");

        assertThrows(IllegalArgumentException.class, () -> authService.register(dupRequest));
    }

    @Test
    void testLoginSuccessfully() {
        RegisterRequest request = new RegisterRequest("login@example.com", "password123", "loginUser");

        authService.register(request);

        UserDto dto = authService.login("login@example.com", "password123", mockResponse);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("login@example.com");
        assertThat(dto.getUsername()).isEqualTo("loginUser");
    }

    @Test
    void testLoginInvalidPasswordThrows() {
        RegisterRequest request = new RegisterRequest("login2@example.com", "password123", "loginUser2");

        authService.register(request);

        assertThrows(IllegalArgumentException.class,
                () -> authService.login("login2@example.com", "wrongpass", mockResponse));
    }

    @Test
    void testGetCurrentUser() {
        RegisterRequest request = new RegisterRequest("me@example.com", "password123", "meUser");

        authService.register(request);

        UserDto dto = authService.getCurrentUser("me@example.com");

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("me@example.com");
        assertThat(dto.getUsername()).isEqualTo("meUser");
    }

    @Test
    void testGetCurrentUserNotFoundThrows() {
        assertThrows(IllegalArgumentException.class, () -> authService.getCurrentUser("missing@example.com"));
    }
}
