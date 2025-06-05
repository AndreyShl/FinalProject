package com.project.demo.controller;

import com.project.demo.model.entity.User;
import com.project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Контроллер-советник, который добавляет информацию о пользователе в модель на всех страницах.
 * Это позволяет отображать баланс пользователя в header на всех страницах сайта.
 */
@ControllerAdvice
public class UserControllerAdvice {

    @Autowired
    private UserService userService;

    /**
     * Adds information about the current user to the model and session on all pages.
     *
     * @param model A model for adding attributes
     * @param session HTTP-session for saving the user
     */
    @ModelAttribute
    public void addUserToModel(Model model, HttpSession session) {
        // Getting an authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Checking if the user is authenticated
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {

            // Getting the user's entity from the database
            User user = userService.findByLogin(authentication.getName());

            // If the user is found, add him to the session and model.
            if (user != null) {
                session.setAttribute("user", user);
                model.addAttribute("currentUser", user);
            }
        }
    }
}