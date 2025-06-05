package com.project.demo.controller;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.ProductImage;
import com.project.demo.model.entity.User;
import com.project.demo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BasketService basketService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getProducts() {
        return "products";
    }

    @GetMapping("/categories")
    public String getCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "products/categories";
    }

    @GetMapping("/category/{id}")
    public String getProductsByCategory(@PathVariable("id") Integer categoryId, Model model) {
        Optional<Category> categoryOpt = categoryService.findCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            return "redirect:/products/categories";
        }

        Category category = categoryOpt.get();
        List<Product> products = productService.getProductsByCategory(category);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("productService", productService); // Pass the productService to the template

        // Check if user is authenticated to determine favorites
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User user = userService.findByLogin(authentication.getName());
            if (user != null) {
                // Create a map of product IDs to favorite status
                Map<Integer, Boolean> favoriteProducts = new HashMap<>();
                for (Product product : products) {
                    favoriteProducts.put(product.getId(), favouriteService.isProductInFavourites(user, product));
                }
                model.addAttribute("favoriteProducts", favoriteProducts);
                model.addAttribute("isAuthenticated", true);
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        return "products/category-products";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable("id") Integer productId, Model model, HttpSession session) {
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            return "redirect:/products/categories";
        }

        // Check if there's an error message in the session
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            // Remove the error message from the session
            session.removeAttribute("errorMessage");
        }

        Product product = productOpt.get();
        model.addAttribute("product", product);
        model.addAttribute("productService", productService); // Pass the productService to the template

        // Check if user is authenticated to determine if product is in favorites
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User user = userService.findByLogin(authentication.getName());
            if (user != null) {
                boolean isInFavorites = favouriteService.isProductInFavourites(user, product);
                model.addAttribute("isInFavorites", isInFavorites);
                model.addAttribute("isAuthenticated", true);
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        return "products/product-detail";
    }

//    @GetMapping("/images/{imageId}")
//    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer imageId) {
//        try {
//            ProductImage image = productImageService.getProductImage(imageId);
//            if (image != null) {
//                Path imagePath = Paths.get(image.getImagePath());
//                byte[] imageBytes = Files.readAllBytes(imagePath);
//
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_JPEG)
//                        .body(imageBytes);
//            }
//            return ResponseEntity.notFound().build();
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    @GetMapping("/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String query) {
        // Search for products by name
        List<Product> products = productService.searchProductsByName(query);
        return products;
    }
}
