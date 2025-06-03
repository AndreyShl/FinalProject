package com.project.demo.controller;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.service.CategoryService;
import com.project.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainPageController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/")
    public String mainPage(Model model) {
        // Get all products for featured products section
        List<Product> featuredProducts = productService.getAllProducts();
        model.addAttribute("featuredProducts", featuredProducts);
        
        // Get all categories for category showcase
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        
        return "pages/mainPage";
    }
}