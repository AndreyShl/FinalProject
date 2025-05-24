package com.project.demo.controller;

import com.project.demo.model.entity.User;
import com.project.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "users/test";
    }

    @PostMapping("/users")
    public String createUser(@RequestBody User user, Model model) {
        userService.saveUser(user);
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "users/test";
    }







}
