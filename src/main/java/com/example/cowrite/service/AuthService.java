package com.example.cowrite.service;

import com.example.cowrite.dto.RegisterRequest;
import com.example.cowrite.dto.TokenDTO;
import com.example.cowrite.dto.UserDto;
import com.example.cowrite.entity.User;
import com.example.cowrite.repository.UserRepository;
import com.example.cowrite.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    @Value("${jwt.cookie.name}")
    private String COOKIE_NAME;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @CacheEvict(value = "users", key = "#request.getEmail()")
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        userRepository.save(user);
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public UserDto login(String email, String password, HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        TokenDTO tokenDto = new TokenDTO(user.getId(),  user.getUsername(), user.getEmail());

        try {
            String userJson = objectMapper.writeValueAsString(tokenDto);
            String token = jwtUtil.generateToken(userJson);

            Cookie cookie = new Cookie(COOKIE_NAME, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(jwtUtil.EXPIRATION_TIME / 1000);
            response.addCookie(cookie);

            return new UserDto(user.getId(), user.getUsername(), user.getEmail());
        }catch (Exception e){
            throw new RuntimeException("Error processing login", e);
        }
    }

    @Cacheable(value = "users", key = "#usernameOrEmail")
    public UserDto getCurrentUser(String usernameOrEmail) {
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}


