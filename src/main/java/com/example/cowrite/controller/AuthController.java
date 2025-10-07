package com.example.cowrite.controller;

import com.example.cowrite.dto.LoginRequest;
import com.example.cowrite.dto.RegisterRequest;
import com.example.cowrite.dto.UserDto;
import com.example.cowrite.entity.User;
import com.example.cowrite.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest request) {
        UserDto userDto = authService.register(request);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request,
                                         HttpServletResponse response) {
        UserDto userDto = authService.login(request.getEmail(), request.getPassword(), response);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        String username = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UserDto userDto = new UserDto(Long.parseLong("123214213312123124"), username, null);
        return ResponseEntity.ok(userDto);
    }
}
