package com.example.adminservice.controller;

import com.example.adminservice.dto.ApiResponse;
import com.example.adminservice.dto.CreateUserRequestDto;
import com.example.adminservice.entity.Role;
import com.example.adminservice.entity.User;
import com.example.adminservice.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    private ObjectMapper objectMapper;
    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    @Test
    void testGetAllUsers_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/users")
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void testCreateUser_success() throws Exception {
// Request
        CreateUserRequestDto request = new CreateUserRequestDto(
                "Hari",
                Role.ADMIN,
                "hari@gmail.com",
                50000.0,
                LocalDate.now()
        );
// Mock response
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Hari");
        user.setRole(Role.ADMIN);
        user.setEmail("hari@gmail.com");
        user.setSalary(50000.0);
        user.setJoiningDate(LocalDate.now());

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("User created successfully");
        apiResponse.setData(user);

        ResponseEntity<ApiResponse<User>> responseEntity =
                new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        Mockito.when(userService.createUser(Mockito.any()))
                .thenReturn(responseEntity);
// Perform API call
        MvcResult result = mockMvc.perform(post("/api/v1/user")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
// ✅ Assert HTTP Status
        assertEquals(201, result.getResponse().getStatus());
// ✅ Convert response JSON to Object
        String json = result.getResponse().getContentAsString();
        ApiResponse<User> actualResponse = objectMapper.readValue(
                json,
                new TypeReference<ApiResponse<User>>() {}
        );
// ✅ Assertions
        assertEquals(true, actualResponse.isSuccess());
        assertEquals("User created successfully", actualResponse.getMessage());
        assertEquals("Hari", actualResponse.getData().getUsername());
        assertEquals("hari@gmail.com", actualResponse.getData().getEmail());
    }

    @Test
    void testGetUser_success() throws Exception {
        UUID id = UUID.randomUUID();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Hari");
        user.setRole(Role.ADMIN);
        user.setEmail("hari@gmail.com");
        user.setSalary(50000.0);
        user.setJoiningDate(LocalDate.now());

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("User retrieved successfully");
        apiResponse.setData(user);

        ResponseEntity<ApiResponse<User>> responseEntity =
                new ResponseEntity<>(apiResponse, HttpStatus.OK);

//        Mockito.when(userService.updateUser(Mockito.eq(id), Mockito.any()))
//                .thenReturn(responseEntity);

        Mockito.when(userService.getUserdetail(Mockito.eq(id)))
                .thenReturn(responseEntity);

        MvcResult result= mockMvc.perform(get("/api/v1/{id}",id)
                .contentType("application/json"))
        .andReturn();

        assertEquals(200,result.getResponse().getStatus());
    }

    @Test
    void testUpdateUser_success() throws Exception {

        UUID id = UUID.randomUUID();

        CreateUserRequestDto request = new CreateUserRequestDto(
                "Hari",
                Role.ADMIN,
                "hari@gmail.com",
                50000.0,
                LocalDate.now()
        );

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Hari");
        user.setRole(Role.ADMIN);
        user.setEmail("hari@gmail.com");
        user.setSalary(50000.0);
        user.setJoiningDate(LocalDate.now());

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("User updated successfully");
        apiResponse.setData(user);

        ResponseEntity<ApiResponse<User>> responseEntity =
                new ResponseEntity<>(apiResponse, HttpStatus.OK);

        // ✅ IMPORTANT: use matchers for ALL params
        Mockito.when(userService.updateUser(Mockito.eq(id), Mockito.any()))
                .thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(
                        put("/api/v1/{id}", id)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        // ✅ Status
        assertEquals(200, result.getResponse().getStatus());

        String json = result.getResponse().getContentAsString();

        ApiResponse<User> actualResponse = objectMapper.readValue(
                json,
                new TypeReference<ApiResponse<User>>() {}
        );

        // ✅ Assertions
        assertTrue(actualResponse.isSuccess());
        assertEquals("User updated successfully", actualResponse.getMessage());
        assertEquals("Hari", actualResponse.getData().getUsername());
        assertEquals("hari@gmail.com", actualResponse.getData().getEmail());
    }

    @Test
    void testDeleteUser_success() throws Exception {
        UUID id = UUID.randomUUID();

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("User deleted successfully");
        apiResponse.setData(null);

        ResponseEntity<ApiResponse<User>> responseEntity =
                new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);

        Mockito.when(userService.deleteUser(Mockito.eq(id)))
                .thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(delete("/api/v1/{id}", id)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(204, result.getResponse().getStatus());
    }
}
