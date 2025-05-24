package com.project.demo.controller;

import com.project.demo.model.entity.User;
import com.project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthUserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "users/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String login, @RequestParam String password, Model model, HttpSession session) {
        User user = userService.authenticateUser(login, password);

        if (user == null) {
            model.addAttribute("error", "Invalid login or password");
            return "users/login";
        }

        session.setAttribute("user", user);

        return "redirect:/users";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users";
    }
}
