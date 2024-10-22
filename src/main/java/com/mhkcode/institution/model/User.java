package com.mhkcode.institution.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private LocalDate dob;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role ;
}
