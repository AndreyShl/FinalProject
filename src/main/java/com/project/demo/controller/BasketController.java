package com.project.demo.controller;

import com.project.demo.model.entity.Basket;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import com.project.demo.service.BasketService;
import com.project.demo.service.ProductService;
import com.project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/basket")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getBasket(Model model, HttpSession session) {
        // Get the authenticated user from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity from the database using the authenticated user's username (login)
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        // Check if there's an error message in the session
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            // Remove the error message from the session
            session.removeAttribute("errorMessage");
        }

        List<Basket> basketItems = basketService.getBasketItemsByUser(user);
        BigDecimal total = basketService.calculateBasketTotal(user);

        model.addAttribute("basketItems", basketItems);
        model.addAttribute("total", total);

        return "products/basket";
    }

    @PostMapping("/add/{productId}")
    public String addToBasket(@PathVariable("productId") Integer productId, 
                             @RequestParam(defaultValue = "1") BigDecimal amount,
                             HttpSession session,
                             Model model) {
        // Get the authenticated user from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity from the database using the authenticated user's username (login)
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            return "redirect:/products/categories";
        }

        try {
            basketService.addProductToBasket(user, productOpt.get(), amount);
            return "redirect:/basket";
        } catch (IllegalArgumentException e) {
            // Add error message to session
            session.setAttribute("errorMessage", e.getMessage());
            // Redirect back to product detail page
            return "redirect:/products/" + productId;
        }
    }

    @PostMapping("/remove/{productId}")
    public String removeFromBasket(@PathVariable("productId") Integer productId,
                                  HttpSession session) {
        // Get the authenticated user from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity from the database using the authenticated user's username (login)
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            return "redirect:/basket";
        }

        basketService.removeProductFromBasket(user, productOpt.get());

        return "redirect:/basket";
    }

    @PostMapping("/update/{productId}")
    public String updateBasketItem(@PathVariable("productId") Integer productId,
                                  @RequestParam BigDecimal amount,
                                  HttpSession session) {
        // Get the authenticated user from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity from the database using the authenticated user's username (login)
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty() || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/basket";
        }

        try {
            basketService.updateProductAmount(user, productOpt.get(), amount);
            return "redirect:/basket";
        } catch (IllegalArgumentException e) {
            // Add error message to session
            session.setAttribute("errorMessage", e.getMessage());
            return "redirect:/basket";
        }
    }
}
