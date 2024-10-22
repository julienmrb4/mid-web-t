package com.mhkcode.institution.repository;

import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByEmailAndPassword(String username,String password);
}
