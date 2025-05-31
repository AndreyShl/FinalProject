package com.project.demo.service;

import com.project.demo.model.entity.User;
import java.math.BigDecimal;
import java.util.List;


public interface UserService {


    User registerUser(String username, String login, String password, String phoneNumber);

    User authenticateUser(String login, String password);

    List<User> getAllUsers();

    User findByLogin(String login);

    User saveUser(User user);

    User topUpBalance(User user, BigDecimal amount);

    User linkPaymentCard(User user, String cardNumber, String cardHolder, String cardExpiry, String cardCvv);

    User removePaymentCard(User user);

    boolean hasPaymentCard(User user);
}
