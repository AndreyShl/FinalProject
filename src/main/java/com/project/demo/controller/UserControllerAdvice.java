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
     * Добавляет информацию о текущем пользователе в модель и сессию на всех страницах.
     *
     * @param model Модель для добавления атрибутов
     * @param session HTTP-сессия для сохранения пользователя
     */
    @ModelAttribute
    public void addUserToModel(Model model, HttpSession session) {
        // Получаем аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверяем, аутентифицирован ли пользователь
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {

            // Получаем сущность пользователя из базы данных
            User user = userService.findByLogin(authentication.getName());

            // Если пользователь найден, добавляем его в сессию и модель
            if (user != null) {
                session.setAttribute("user", user);
                model.addAttribute("currentUser", user);
            }
        }
    }
}