package com.mhkcode.institution.model;

//import com.mhkcode.institution.listener.AuditListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
//@EntityListeners(AuditListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;
    private String email;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private LocalDate dob;
    private String password;
    private LocalDateTime createdAt;
    private boolean active;
    @Enumerated(EnumType.STRING)
    private UserRole role ;
    @Column(columnDefinition = "TEXT")
    private String profilePicture;
}
