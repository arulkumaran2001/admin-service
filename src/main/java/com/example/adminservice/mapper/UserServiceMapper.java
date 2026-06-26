package com.example.adminservice.mapper;

import com.example.adminservice.dto.ApiResponse;
import com.example.adminservice.dto.CreateUserRequestDto;
import com.example.adminservice.dto.CreateUserRespDto;
import com.example.adminservice.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
public class UserServiceMapper {

    public User mapToEntity(CreateUserRequestDto request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        user.setEmail(request.getEmail());
        user.setSalary(request.getSalary());
        user.setJoiningDate(LocalDate.now());
        return user;
    }

    public CreateUserRespDto mapToDto(User savedUser) {
        CreateUserRespDto response=new CreateUserRespDto();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setRole(savedUser.getRole());
        response.setEmail(savedUser.getEmail());
        response.setSalary(savedUser.getSalary());
        response.setJoiningDate(savedUser.getJoiningDate());
        return response;
    }
    public ResponseEntity<ApiResponse<User>> mapToApiResponse(User user, String message, HttpStatus status) {
        ApiResponse<User> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(user);
        response.setError(null);
        return new ResponseEntity<>(response, status);
//        return response;
    }
}
