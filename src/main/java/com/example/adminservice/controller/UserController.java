package com.example.adminservice.controller;

import com.example.adminservice.dto.ApiResponse;
import com.example.adminservice.dto.CreateUserRequestDto;
import com.example.adminservice.dto.CreateUserRespDto;
import com.example.adminservice.entity.User;
import com.example.adminservice.repository.UserRepository;
import com.example.adminservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUserRequestDto request) {
        return userService.createUser(request);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable UUID id) {
        return userService.getUserdetail(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable UUID id) {
         return userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable UUID id,@RequestBody CreateUserRequestDto request){
        return userService.updateUser(id,request);
    }
}
