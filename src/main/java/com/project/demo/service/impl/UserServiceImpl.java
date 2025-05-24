package com.project.demo.service.impl;

import com.project.demo.model.entity.User;
import com.project.demo.model.repository.UsersRepository;
import com.project.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public User registerUser(String username, String login, String password, String phoneNumber) {


        User existingUser = usersRepository.findByLogin(login);
        if (existingUser != null) {
            throw new IllegalArgumentException("Login already exists");
        }


        User newUser = new User();
        newUser.setUsername(username);
        newUser.setLogin(login);
        newUser.setPassword(password);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setRole(User.Role.USER);
        newUser.setBalance(new BigDecimal("0.00"));
        newUser.setStatus("ACTIVE");


        return usersRepository.save(newUser);
    }

    @Override
    public User authenticateUser(String login, String password) {
        User user = usersRepository.findByLogin(login);
        
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public User findByLogin(String login) {
        return usersRepository.findByLogin(login);
    }

    @Override
    public User saveUser(User user) {
        return usersRepository.save(user);
    }
}