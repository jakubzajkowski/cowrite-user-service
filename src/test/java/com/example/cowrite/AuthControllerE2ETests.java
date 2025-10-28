package com.example.cowrite;

import com.example.cowrite.dto.LoginRequest;
import com.example.cowrite.dto.RegisterRequest;
import com.example.cowrite.entity.User;
import com.example.cowrite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
class AuthControllerE2ETests extends AbstractTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUsername("tester");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("tester"));

        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    void shouldLoginUserAndSetCookie() throws Exception {
        User user = new User();
        user.setEmail("login123@example.com");
        user.setUsername("loginUser123");
        user.setPassword("$2a$12$6WnoDf6OQku.E/HM5poHiugXo6WA6JiFsRSdEB.o7K88tHdZhIsOq");
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login123@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("COWRITE_SESSION_ID"))
                .andExpect(jsonPath("$.email").value("login123@example.com"))
                .andExpect(jsonPath("$.username").value("loginUser123"));
    }

    @Test
    void shouldGetCurrentUserWhenAuthenticated() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("me@example.com");
        register.setUsername("meUser");
        register.setPassword("password123");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail("me@example.com");
        login.setPassword("password123");

        String cookie = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookie("COWRITE_SESSION_ID")
                .getValue();

        mockMvc.perform(get("/me")
                        .cookie(new jakarta.servlet.http.Cookie("COWRITE_SESSION_ID", cookie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me@example.com"))
                .andExpect(jsonPath("$.username").value("meUser"));
    }
}
