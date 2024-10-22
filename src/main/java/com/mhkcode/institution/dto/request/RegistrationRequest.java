package com.mhkcode.institution.dto.request;

import com.mhkcode.institution.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
public class RegistrationRequest {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private LocalDate dob;
    private String password;
    private UserRole role ;
}
