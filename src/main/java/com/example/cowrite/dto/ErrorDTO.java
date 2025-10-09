package com.example.cowrite.dto;

import java.time.LocalDateTime;

public class ErrorDTO {
    private LocalDateTime timestamp;
    private int code;
    private String message;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorDTO(LocalDateTime timestamp, int code, String message) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
    }
}
