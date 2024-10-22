package com.mhkcode.institution.controller;


import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";  // This should match the filename: register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationRequest request, Model model) {
        var response = userService.addUser(request);

        if ("201".equals(response.getResponseStatus())) {
            return "redirect:/login?registered=true";
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
        LoginResponse user = userService.login(request);

        if (user != null) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
