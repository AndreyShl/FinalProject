package com.project.demo.service.impl;

import com.project.demo.model.entity.User;
import com.project.demo.model.repository.UsersRepository;
import com.project.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String username, String login, String password, String phoneNumber) {


        User existingUser = usersRepository.findByLogin(login);
        if (existingUser != null) {
            throw new IllegalArgumentException("Login already exists");
        }


        User newUser = new User();
        newUser.setUsername(username);
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPhoneNumber(phoneNumber);
        newUser.setRole(User.Role.USER);
        newUser.setBalance(new BigDecimal("0.00"));
        newUser.setStatus("ACTIVE");


        return usersRepository.save(newUser);
    }

    @Override
    public User authenticateUser(String login, String password) {
        User user = usersRepository.findByLogin(login);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
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
        // Check if the password is already encoded (starts with $2a$)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return usersRepository.save(user);
    }
}
