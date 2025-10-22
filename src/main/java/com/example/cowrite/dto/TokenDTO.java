package com.example.cowrite.dto;

public class TokenDTO {
    private Long id;
    private String email;
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TokenDTO() {}

    public TokenDTO(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }
}
