package com.project.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthUserController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "users/login";
    }

    // Spring Security will handle login processing and logout
    // No need for custom login and logout methods
}
