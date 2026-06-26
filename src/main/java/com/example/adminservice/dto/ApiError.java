package com.example.adminservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ApiError(int status, String error, String message, LocalDateTime timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }
}
