package com.example.adminservice.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ApiError error;


    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data, ApiError error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error= error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }
}
