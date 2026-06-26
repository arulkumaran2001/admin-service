package com.example.adminservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "users",uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Role role;

    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@(gmail\\.com|yahoo\\.in|outlook\\.com)$",
            message = "invalid email format"
    )
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false)
    private LocalDate joiningDate;
}
