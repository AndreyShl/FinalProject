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

    @Override
    public User topUpBalance(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        return usersRepository.save(user);
    }

    @Override
    public User linkPaymentCard(User user, String cardNumber, String cardHolder, String cardExpiry, String cardCvv) {
        // Basic validation
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number is required");
        }
        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Card holder name is required");
        }
        if (cardExpiry == null || cardExpiry.trim().isEmpty()) {
            throw new IllegalArgumentException("Card expiry date is required");
        }
        if (cardCvv == null || cardCvv.trim().isEmpty()) {
            throw new IllegalArgumentException("Card CVV is required");
        }

        // Set card details
        user.setCardNumber(cardNumber);
        user.setCardHolder(cardHolder);
        user.setCardExpiry(cardExpiry);
        user.setCardCvv(cardCvv);

        return usersRepository.save(user);
    }

    @Override
    public User removePaymentCard(User user) {
        user.setCardNumber(null);
        user.setCardHolder(null);
        user.setCardExpiry(null);
        user.setCardCvv(null);

        return usersRepository.save(user);
    }

    @Override
    public boolean hasPaymentCard(User user) {
        return user.getCardNumber() != null && !user.getCardNumber().trim().isEmpty();
    }

    @Override
    public User deductFromBalance(User user, BigDecimal amount) throws InsufficientBalanceException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Проверяем, достаточно ли средств на балансе
        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance: " + user.getBalance() + ", Required: " + amount);
        }

        // Списываем средства с баланса
        BigDecimal newBalance = user.getBalance().subtract(amount);
        user.setBalance(newBalance);
        return usersRepository.save(user);
    }
}
