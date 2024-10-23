
package com.mhkcode.institution.controller;

import com.mhkcode.institution.dto.request.ChangePasswordRequest;
import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.model.User;
import com.mhkcode.institution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            return "redirect:/admin-dashboard";
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

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, Model model) {
        LoginResponse loginResponse = userService.login(request);
        if ("200".equals(loginResponse.getResponseStatus())) {
            return switch (loginResponse.getRole()) {
                case "ADMIN" -> "redirect:/admin-dashboard";
                case "AGENT" -> "redirect:/agent-dashboard";
                case "INSTITUTION" -> "redirect:/inst-dashboard";
                default -> "redirect:/dashboard";
            };
        } else {
            model.addAttribute("error", loginResponse.getMessage());
            return "login";
        }
    }

    // Dashboard routes
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dash";
    }

    @GetMapping("/agent-dashboard")
    public String agentDashboard() {
        return "agent-dash";
    }

    @GetMapping("/inst-dashboard")
    public String instDashboard() {
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

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "edit-user";
        }
        return "redirect:/users?error=User+not+found";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute RegistrationRequest request, Model model) {
        var response = userService.updateUser(request);
        if ("200".equals(response.getResponseStatus())) {
            return "redirect:/users?success=User+updated";
        }
        model.addAttribute("error", response.getMessage());
        return "edit-user";
    }

    @PostMapping("/users/delete/{email}")
    public String deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return "redirect:/users?success=User+deleted";
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


}