package com.mhkcode.institution.service.impl;

import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.dto.response.RegistrationResponse;
import com.mhkcode.institution.model.User;
import com.mhkcode.institution.repository.UserRepository;
import com.mhkcode.institution.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public RegistrationResponse addUser(RegistrationRequest request) {
        logger.info("Adding new user: {}", request.getEmail());
        RegistrationResponse response = new RegistrationResponse();

        try {
            // Check if user exists
            User existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser != null) {
                response.setResponseStatus("400");
                response.setMessage("Email already exists");
                return response;
            }

            // Create and save new user
            User user = new User();
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            user.setEmail(request.getEmail());
            user.setDob(request.getDob());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(request.getPassword());
            user.setRole(request.getRole());

            userRepository.save(user);

            response.setResponseStatus("201");
            response.setMessage("Registration successful!");

        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage());
            response.setResponseStatus("500");
            response.setMessage("Registration failed. Please try again.");
        }

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Processing login for user: {}", request.getEmail());
        LoginResponse response = new LoginResponse();

        try {
            User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());

            if (user != null) {
                response.setResponseStatus("200");
                response.setMessage("Login successful");

            } else {
                response.setResponseStatus("401");
                response.setMessage("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            response.setResponseStatus("500");
            response.setMessage("Error during login");
        }

        return response;
    }
}