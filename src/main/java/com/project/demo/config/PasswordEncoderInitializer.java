package com.project.demo.config;

import com.project.demo.model.entity.User;
import com.project.demo.model.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordEncoderInitializer implements CommandLineRunner {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<User> users = usersRepository.findAll();

        // Encode existing passwords for all users
        for (User user : users) {
            // Check if the password is not already encoded
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                usersRepository.save(user);
            }
        }

        // Create admin user if none exists
        boolean adminExists = users.stream()
                .anyMatch(user -> User.Role.ADMIN.equals(user.getRole()));

        if (!adminExists) {
            User adminUser = new User();
            adminUser.setUsername("Admin");
            adminUser.setLogin("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRole(User.Role.ADMIN);
            adminUser.setStatus("ACTIVE");
            adminUser.setPhoneNumber("123456789");
            adminUser.setBalance(java.math.BigDecimal.ZERO);
            usersRepository.save(adminUser);

            System.out.println("Created default admin user with login: admin and password: admin");
        }
    }
}
