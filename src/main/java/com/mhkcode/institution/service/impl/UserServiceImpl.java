package com.mhkcode.institution.service.impl;

import com.mhkcode.institution.dto.request.ChangePasswordRequest;
import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.ChangePasswordResponse;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.dto.response.RegistrationResponse;
import com.mhkcode.institution.model.User;
import com.mhkcode.institution.repository.UserRepository;
import com.mhkcode.institution.security.JwtUtil;
import com.mhkcode.institution.service.AuditService;
import com.mhkcode.institution.service.EmailService;
import com.mhkcode.institution.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuditService auditService;


//    @Override
//    public RegistrationResponse addUser(RegistrationRequest request) {
//        logger.info("Adding new user: {}", request.getEmail());
//        RegistrationResponse response = new RegistrationResponse();
//
//        try {
//            // Check if user exists
//            User existingUser = userRepository.findByEmail(request.getEmail());
//            if (existingUser != null) {
//                response.setResponseStatus("400");
//                response.setMessage("Email already exists");
//                return response;
//            }
//
//            // Create and save new user
//            User user = new User();
//            user.setFirstname(request.getFirstname());
//            user.setLastname(request.getLastname());
//            user.setEmail(request.getEmail());
//            user.setDob(request.getDob());
//            user.setPhoneNumber(request.getPhoneNumber());
//            user.setPassword(request.getPassword());
//            user.setRole(request.getRole());
//
//            userRepository.save(user);
//
//            response.setResponseStatus("201");
//            response.setMessage("Registration successful!");
//
//        } catch (Exception e) {
//            logger.error("Error during registration: {}", e.getMessage());
//            response.setResponseStatus("500");
//            response.setMessage("Registration failed. Please try again.");
//        }
//
//        return response;
//    }

//    @Override
//    public LoginResponse login(LoginRequest request) {
//        logger.info("Processing login for user: {}", request.getEmail());
//        LoginResponse response = new LoginResponse();
//
//        // Validate email and password before proceeding
//        if (request.getEmail() == null || request.getEmail().isEmpty()) {
//            response.setResponseStatus("400");
//            response.setMessage("Email is required");
//            return response;
//        }
//        if (request.getPassword() == null || request.getPassword().isEmpty()) {
//            response.setResponseStatus("400");
//            response.setMessage("Password is required");
//            return response;
//        }
//
//        try {
//            // Find user by email and password
//            User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
//
//            if (user != null) {
//                // Login successful
//                response.setResponseStatus("200");
//                response.setMessage("Login successful");
//                response.setRole(user.getRole().name());
//            } else {
//                // Invalid credentials
//                response.setResponseStatus("401");
//                response.setMessage("Invalid email or password");
//            }
//        } catch (Exception e) {
//            // Log the error and respond with an internal server error
//            logger.error("Error during login: {}", e.getMessage());
//            response.setResponseStatus("500");
//            response.setMessage("Internal server error during login");
//        }
//
//        return response;
//    }

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
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());

            // Set default values for audit fields
            user.setCreatedAt(LocalDateTime.now());
            user.setActive(true);

            userRepository.save(user);

            // Add audit log after successful save
            auditService.logAudit(user, "CREATE", request.getEmail());

            response.setResponseStatus("201");
            response.setMessage("Registration successful!");

        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
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
            // Find user by email
            User user = userRepository.findByEmail(request.getEmail());

            if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Generate JWT token
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

                // Set response
                response.setResponseStatus("200");
                response.setMessage("Login successful");
                response.setRole(user.getRole().name());
                response.setToken(token);
                //claims.put("role", user.getRole());  // Should be "ADMIN", "AGENT", or "INSTITUTION"
            } else {
                response.setResponseStatus("401");
                response.setMessage("Invalid email or password");
            }
        } catch (Exception e) {
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
                response.setResponseStatus("400");
                response.setMessage("User not found bad email");
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
    public ChangePasswordResponse resetPassword(ChangePasswordRequest request) {
        logger.info("Processing password change for user: {}", request.getEmail());
        ChangePasswordResponse response = new ChangePasswordResponse();
        String newPassword = "12345";  // You may want to generate a random password here for better security.

        try {
            User user = userRepository.findByEmail(request.getEmail());

            if (user == null) {
                response.setResponseStatus("400");
                response.setMessage("User not found. Bad email.");
                return response;
            }

            // Update password
            //user.setPassword(newPassword);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Send email with new password
            String subject = "Password Reset Confirmation";
            String body = "Dear " + user.getFirstname() + ",\n\nYour password has been reset successfully. " +
                    "Your new password is: " + newPassword + "\n\nPlease log in and change this password.";
            emailService.sendEmail(user.getEmail(), subject, body);

            response.setResponseStatus("200");
            response.setMessage("Your new password has been sent to your email.");

        } catch (Exception e) {
            logger.error("Error during password reset: {}", e.getMessage());
            response.setResponseStatus("500");
            response.setMessage("Failed to reset password. Please try again.");
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
            User existingUser = userRepository.findByUserId(request.getUserId());
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
            existingUser.setRole(request.getRole());

            userRepository.save(existingUser);
            auditService.logAudit(existingUser, "UPDATE", request.getEmail());
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
    public void deleteUser(Long id) {
        logger.info("Deleting user with email: {}", id);
        try {
            User user = userRepository.findByUserId(id);
            if (user != null) {
                auditService.logAudit(user, "DELETE", user.getEmail());
                userRepository.delete(user);
                logger.info("User deleted successfully: {}", id);
            } else {
                logger.warn("No user found with email: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user");
        }
    }

    @Override
    public User setProfile(User user, MultipartFile file) throws IOException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (file != null && !file.isEmpty()) {
            // Convert MultipartFile to Base64 string
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            user.setProfilePicture("data:image/" + getFileExtension(file.getOriginalFilename()) + ";base64," + base64Image);
            return userRepository.save(user);
        }
        return user;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

}