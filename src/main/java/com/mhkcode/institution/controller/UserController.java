
package com.mhkcode.institution.controller;

import com.mhkcode.institution.dto.request.ChangePasswordRequest;
import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.ProfilePictureRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.model.User;
import com.mhkcode.institution.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller

public class UserController {

    @Autowired
    private UserService userService;

    // Default landing page
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }


    // Authentication related endpoints
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationRequest request, Model model) {
        var response = userService.addUser(request);
        if ("201".equals(response.getResponseStatus())) {
            return "redirect:/users";
        } else {
            model.addAttribute("error", response.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//        LoginResponse loginResponse = userService.login(request);
//        if ("200".equals(loginResponse.getResponseStatus())) {
//            // Token and role are now included in the response
//            return ResponseEntity.ok(loginResponse);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(loginResponse);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);

        // Add logging
        System.out.println("Login response: " + loginResponse);

        if ("200".equals(loginResponse.getResponseStatus())) {
            // Debug print the token and role
            System.out.println("Token: " + loginResponse.getToken());
            System.out.println("Role: " + loginResponse.getRole());
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(loginResponse);
        }
    }

    // Dashboard routes
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

//    @GetMapping("/admin-dashboard")
//    public String adminDashboard() {
//        return "admin-dash";
//    }
@GetMapping("/admin-dashboard")
public String adminDashboard(@RequestHeader(name = "Authorization", required = false) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return "admin-dash";
    }
    return "admin-dash";
}

//    @GetMapping("/agent-dashboard")
//    public String agentDashboard() {
//        return "agent-dash";
//    }
@GetMapping("/agent-dashboard")
public String agentDashboard(@RequestHeader(name = "Authorization", required = false) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return "agent-dash";
    }
    return "agent-dash";
}

//    @GetMapping("/inst-dashboard")
//    public String instDashboard() {
//        return "institution-dash";
//    }

    @GetMapping("/inst-dashboard")
    public String instDashboard(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "institution-dash";
        }
        return "institution-dash";
    }

    // User management endpoints
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.allUsers();
        model.addAttribute("users", users);
        return "users-list";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "user-details";
        }
        return "redirect:/users?error=User+not+found";
    }

    @GetMapping("/users/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "update-user";
        }
        return "redirect:/users?error=User+not+found";
    }

    @PostMapping("/users/update/{userId}")
    public String updateUser(@PathVariable Long userId, @ModelAttribute RegistrationRequest request, RedirectAttributes redirectAttributes) {
        var response = userService.updateUser(request);
        if ("200".equals(response.getResponseStatus())) {
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
            return "redirect:/users";
        }
        redirectAttributes.addFlashAttribute("error", response.getMessage());
        return "redirect:/users/update/" + userId;
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        return "redirect:/users";
    }

    // Password management
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request, Model model) {
        var response = userService.changePassword(request);
        if ("200".equals(response.getResponseStatus())) {
            return "redirect:/dashboard?success=Password+changed";
        }
        model.addAttribute("error", response.getMessage());
        return "change-password";
    }

    @GetMapping("/forgot-password")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute ChangePasswordRequest request, Model model) {
        var response = userService.resetPassword(request);
        if ("200".equals(response.getResponseStatus())) {
            return "redirect:/dashboard?success=Password+changed";
        }
        model.addAttribute("error", response.getMessage());
        return "change-password";
    }

    @PostMapping("/upload-profile-picture")
    public String updateProfilePicture(
            @RequestParam("profilePicture") MultipartFile file,
            @RequestParam(value = "email", required = false) String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
                return "redirect:/admin-dashboard";
            }

            // First try to get email from the session
            if (email == null) {
                email = (String) session.getAttribute("userEmail");
            }

            // Validate we have a user email
            if (email == null) {
                redirectAttributes.addFlashAttribute("error", "User not authenticated");
                return "redirect:/login";
            }

            User user = userService.findByEmail(email);
            if (user != null) {
                userService.setProfile(user, file);
                redirectAttributes.addFlashAttribute("success", "Profile picture updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile picture: " + e.getMessage());
        }
        return "redirect:/admin-dashboard";
    }
}