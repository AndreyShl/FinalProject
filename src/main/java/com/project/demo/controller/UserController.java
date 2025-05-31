package com.project.demo.controller;

import com.project.demo.model.entity.User;
import com.project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
    
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Get the user entity from the database
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // Update the user in the session
        session.setAttribute("user", user);
        
        // Check if the user has a payment card
        boolean hasCard = userService.hasPaymentCard(user);
        model.addAttribute("hasCard", hasCard);
        
        return "users/profile";
    }
    
    @PostMapping("/profile/topup")
    public String topUpBalance(@RequestParam("amount") BigDecimal amount, 
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Get the user entity from the database
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Top up the balance
            user = userService.topUpBalance(user, amount);
            
            // Update the user in the session
            session.setAttribute("user", user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Баланс успешно пополнен на " + amount);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/profile/addCard")
    public String addPaymentCard(@RequestParam("cardNumber") String cardNumber,
                                @RequestParam("cardHolder") String cardHolder,
                                @RequestParam("cardExpiry") String cardExpiry,
                                @RequestParam("cardCvv") String cardCvv,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Get the user entity from the database
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Link the payment card
            user = userService.linkPaymentCard(user, cardNumber, cardHolder, cardExpiry, cardCvv);
            
            // Update the user in the session
            session.setAttribute("user", user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Карта успешно привязана");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/profile/removeCard")
    public String removePaymentCard(HttpSession session, RedirectAttributes redirectAttributes) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Get the user entity from the database
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // Remove the payment card
        user = userService.removePaymentCard(user);
        
        // Update the user in the session
        session.setAttribute("user", user);
        
        redirectAttributes.addFlashAttribute("successMessage", "Карта успешно удалена");
        
        return "redirect:/profile";
    }
}