package com.project.demo.controller;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import com.project.demo.service.BasketService;
import com.project.demo.service.CategoryService;
import com.project.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
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

        model.addAttribute("product", productOpt.get());
        return "products/product-detail";
    }
}
