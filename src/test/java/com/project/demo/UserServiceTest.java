package com.project.demo;

import com.project.demo.model.entity.User;
import com.project.demo.model.repository.UsersRepository;
import com.project.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        // Arrange
        String username = "testUser";
        String login = "testLogin";
        String password = "testPassword";
        String phoneNumber = "1234567890";
        String encodedPassword = "encodedPassword";
        
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername(username);
        savedUser.setLogin(login);
        savedUser.setPassword(encodedPassword);
        savedUser.setPhoneNumber(phoneNumber);
        savedUser.setRole(User.Role.USER);
        savedUser.setBalance(new BigDecimal("0.00"));
        savedUser.setStatus("ACTIVE");
        
        // Mock repository and encoder behavior
        when(usersRepository.findByLogin(login)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(usersRepository.save(any(User.class))).thenReturn(savedUser);
        
        // Act
        User createdUser = userService.registerUser(username, login, password, phoneNumber);
        
        // Assert
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertEquals(login, createdUser.getLogin());
        assertEquals(encodedPassword, createdUser.getPassword());
        assertEquals(phoneNumber, createdUser.getPhoneNumber());
        assertEquals(User.Role.USER, createdUser.getRole());
        assertEquals(new BigDecimal("0.00"), createdUser.getBalance());
        assertEquals("ACTIVE", createdUser.getStatus());
        
        // Verify interactions
        verify(usersRepository).findByLogin(login);
        verify(passwordEncoder).encode(password);
        verify(usersRepository).save(any(User.class));
    }
    
    @Test
    public void testCreateUserWithExistingLogin() {
        // Arrange
        String username = "testUser";
        String login = "existingLogin";
        String password = "testPassword";
        String phoneNumber = "1234567890";
        
        User existingUser = new User();
        existingUser.setLogin(login);
        
        // Mock repository behavior
        when(usersRepository.findByLogin(login)).thenReturn(existingUser);
        
        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(username, login, password, phoneNumber);
        });
        
        assertEquals("Login already exists", exception.getMessage());
        
        // Verify interactions
        verify(usersRepository).findByLogin(login);
        verify(usersRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testAuthenticateUserSuccess() {
        // Arrange
        String login = "testLogin";
        String password = "testPassword";
        String encodedPassword = "encodedPassword";
        
        User user = new User();
        user.setId(1);
        user.setLogin(login);
        user.setPassword(encodedPassword);
        
        // Mock repository and encoder behavior
        when(usersRepository.findByLogin(login)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        
        // Act
        User authenticatedUser = userService.authenticateUser(login, password);
        
        // Assert
        assertNotNull(authenticatedUser);
        assertEquals(login, authenticatedUser.getLogin());
        
        // Verify interactions
        verify(usersRepository).findByLogin(login);
        verify(passwordEncoder).matches(password, encodedPassword);
    }
    
    @Test
    public void testAuthenticateUserFailure() {
        // Arrange
        String login = "testLogin";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";
        
        User user = new User();
        user.setId(1);
        user.setLogin(login);
        user.setPassword(encodedPassword);
        
        // Mock repository and encoder behavior
        when(usersRepository.findByLogin(login)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);
        
        // Act
        User authenticatedUser = userService.authenticateUser(login, password);
        
        // Assert
        assertNull(authenticatedUser);
        
        // Verify interactions
        verify(usersRepository).findByLogin(login);
        verify(passwordEncoder).matches(password, encodedPassword);
    }
    
    @Test
    public void testAuthenticateUserNotFound() {
        // Arrange
        String login = "nonExistentLogin";
        String password = "testPassword";
        
        // Mock repository behavior
        when(usersRepository.findByLogin(login)).thenReturn(null);
        
        // Act
        User authenticatedUser = userService.authenticateUser(login, password);
        
        // Assert
        assertNull(authenticatedUser);
        
        // Verify interactions
        verify(usersRepository).findByLogin(login);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}