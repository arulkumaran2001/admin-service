package com.example.adminservice.dto;

import com.example.adminservice.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateUserRequestDto {
    @NotBlank(message = "Name is required")
    private String username;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "role is required")
    private Role role;

    @NotBlank(message = "email is required")
    private String email;

    @NotNull(message = "salary is required")
    private Double salary;


    private LocalDate joiningDate;
}
