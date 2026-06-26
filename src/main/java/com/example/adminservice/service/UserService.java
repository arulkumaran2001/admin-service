package com.example.adminservice.service;

import com.example.adminservice.dto.ApiResponse;
import com.example.adminservice.dto.CreateUserRequestDto;
import com.example.adminservice.dto.CreateUserRespDto;
import com.example.adminservice.entity.User;
import com.example.adminservice.exception.ResourceNotFoundException;
import com.example.adminservice.mapper.UserServiceMapper;
import com.example.adminservice.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceMapper userServiceMapper;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<ApiResponse<User>> createUser(CreateUserRequestDto request) {
        User user=userServiceMapper.mapToEntity(request);
        User createdUser = userRepository.save(user);
        return userServiceMapper.mapToApiResponse(createdUser, "User created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse<User>> getUserdetail(UUID id){
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+id));
        return userServiceMapper.mapToApiResponse(user, "User retrieved successfully", HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<User>> deleteUser(UUID id){
        userRepository.delete(userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+id)));
        return userServiceMapper.mapToApiResponse(null, "User deleted successfully", HttpStatus.NO_CONTENT);
    }
    public ResponseEntity<ApiResponse<User>> updateUser(UUID id,CreateUserRequestDto request) {
        userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+ id));
        User user=userServiceMapper.mapToEntity(request);
        User updatedUser = userRepository.save(user);
        return userServiceMapper.mapToApiResponse(updatedUser, "User updated successfully", HttpStatus.OK);
    }
}
