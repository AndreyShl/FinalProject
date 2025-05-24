package com.project.demo.service;

import com.project.demo.model.entity.User;
import java.util.List;


public interface UserService {
    

    User registerUser(String username, String login, String password, String phoneNumber);

    User authenticateUser(String login, String password);

    List<User> getAllUsers();

    User findByLogin(String login);

    User saveUser(User user);
}