package com.example.adminservice.dto;

import com.example.adminservice.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateUserRespDto {
    private UUID id;
    private String username;
    private Role role;
    private String email;
    private Double salary;
    private LocalDate joiningDate;
}
