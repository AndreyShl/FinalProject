package com.project.demo.controller;

import com.project.demo.model.entity.Favourite;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import com.project.demo.service.FavouriteService;
import com.project.demo.service.ProductService;
import com.project.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/favourites")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String getFavourites(Model model) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        // Get user's favorites
        List<Favourite> favourites = favouriteService.getFavouritesByUser(user);
        
        // Add to model
        model.addAttribute("favourites", favourites);
        model.addAttribute("productService", productService); // For calculating discounted prices
        
        return "products/favourites";
    }

    @PostMapping("/add/{productId}")
    public String addToFavourites(@PathVariable("productId") Integer productId, 
                                 @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        // Get the product
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            return "redirect:/products/categories";
        }

        // Add to favorites
        favouriteService.addToFavourites(user, productOpt.get());

        // Redirect back to the page the user was on, or to the favorites page
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/favourites";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromFavourites(@PathVariable("productId") Integer productId,
                                      @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Get the user entity
        User user = userService.findByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        // Get the product
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            return "redirect:/favourites";
        }

        // Remove from favorites
        favouriteService.removeFromFavourites(user, productOpt.get());

        // Redirect back to the page the user was on, or to the favorites page
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/favourites";
    }
}