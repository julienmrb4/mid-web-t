package com.mhkcode.institution.service.impl;

import com.mhkcode.institution.dto.request.ChangePasswordRequest;
import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.ChangePasswordResponse;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.dto.response.RegistrationResponse;
import com.mhkcode.institution.model.User;
import com.mhkcode.institution.repository.UserRepository;
import com.mhkcode.institution.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        // Validate email and password before proceeding
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            response.setResponseStatus("400");
            response.setMessage("Email is required");
            return response;
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            response.setResponseStatus("400");
            response.setMessage("Password is required");
            return response;
        }

        try {
            // Find user by email and password
            User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());

            if (user != null) {
                // Login successful
                response.setResponseStatus("200");
                response.setMessage("Login successful");
                response.setRole(user.getRole().name());
            } else {
                // Invalid credentials
                response.setResponseStatus("401");
                response.setMessage("Invalid email or password");
            }
        } catch (Exception e) {
            // Log the error and respond with an internal server error
            logger.error("Error during login: {}", e.getMessage());
            response.setResponseStatus("500");
            response.setMessage("Internal server error during login");
        }

        return response;
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        logger.info("Processing password change for user: {}", request.getEmail());
        ChangePasswordResponse response = new ChangePasswordResponse();

        try {
            User user = userRepository.findByEmail(request.getEmail());

            if (user == null) {
                response.setResponseStatus("404");
                response.setMessage("User not found");
                return response;
            }

            // Verify old password
            if (!user.getPassword().equals(request.getCurrentPassword())) {
                response.setResponseStatus("401");
                response.setMessage("Current password is incorrect");
                return response;
            }

            // Update password
            user.setPassword(request.getNewPassword());
            userRepository.save(user);

            response.setResponseStatus("200");
            response.setMessage("Password changed successfully");

        } catch (Exception e) {
            logger.error("Error during password change: {}", e.getMessage());
            response.setResponseStatus("500");
            response.setMessage("Failed to change password. Please try again.");
        }

        return response;
    }

    @Override
    public User getById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        User user = userRepository.findByUserId(id);
        if (user == null) {
            logger.warn("No user found with ID: {}", id);
        }
        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public RegistrationResponse updateUser(RegistrationRequest request) {
        logger.info("Updating user: {}", request.getEmail());
        RegistrationResponse response = new RegistrationResponse();

        try {
            User existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser == null) {
                response.setResponseStatus("404");
                response.setMessage("User not found");
                return response;
            }

            // Update user details
            existingUser.setEmail(request.getEmail());
            existingUser.setFirstname(request.getFirstname());
            existingUser.setLastname(request.getLastname());
            existingUser.setDob(request.getDob());
            existingUser.setPhoneNumber(request.getPhoneNumber());

            userRepository.save(existingUser);

            response.setResponseStatus("200");
            response.setMessage("User updated successfully");

        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            response.setResponseStatus("500");
            response.setMessage("Update failed. Please try again.");
        }

        return response;
    }

    @Override
    public void deleteUser(String email) {
        logger.info("Deleting user with email: {}", email);
        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                userRepository.delete(user);
                logger.info("User deleted successfully: {}", email);
            } else {
                logger.warn("No user found with email: {}", email);
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user");
        }
    }

}