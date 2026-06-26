package com.example.adminservice.service;

import com.example.adminservice.dto.ApiResponse;
import com.example.adminservice.dto.CreateUserRequestDto;
import com.example.adminservice.entity.Role;
import com.example.adminservice.entity.User;
import com.example.adminservice.mapper.UserServiceMapper;
import com.example.adminservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserServiceMapper userServiceMapper;

    @InjectMocks
    UserService userService;




    @Test
    void testgetAllUsers() {
        User user1 = new User();
        user1.setUsername("Arul");
        user1.setRole(Role.EMPLOYEE);
        user1.setEmail("arul@gmail.com");
        user1.setSalary(10000.0);
        user1.setJoiningDate(LocalDate.now());

        User user2 = new User();
        user2.setUsername("Hari");
        user2.setRole(Role.ADMIN);
        user2.setEmail("hari@gmail.com");
        user2.setSalary(50000.0);
        user2.setJoiningDate(LocalDate.now());

        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result= userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Arul", result.get(0).getUsername());
        assertEquals("Hari", result.get(1).getUsername());
    }

    @Test
    void teatcreateUser() {

        CreateUserRequestDto request = new CreateUserRequestDto(
                "Arul",
                Role.EMPLOYEE,
                "arul@gmail.com",
                10000.0,
                LocalDate.now()
        );

        User user1 = new User();
        user1.setUsername("Arul");
        user1.setRole(Role.EMPLOYEE);
        user1.setEmail("arul@gmail.com");
        user1.setSalary(10000.0);
        user1.setJoiningDate(LocalDate.now());

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("User created successfully");
        apiResponse.setData(user1);

        ResponseEntity<ApiResponse<User>> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

        when(userServiceMapper.mapToEntity(request)).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);
        when(userServiceMapper.mapToApiResponse(user1, "User created successfully", HttpStatus.CREATED)).thenReturn(responseEntity);

        ResponseEntity<ApiResponse<User>> result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("User created successfully", result.getBody().getMessage());
        assertEquals("Arul", result.getBody().getData().getUsername());

    }

    @Test
    void getUserdetail() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }
}