package com.project.demo.controller;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.service.AdminService;
import com.project.demo.service.CategoryService;
import com.project.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AdminService adminService;

    // Admin dashboard
    @GetMapping
    public String adminDashboard(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        AdminService.AdminDashboardData dashboardData = adminService.getDashboardData();

        model.addAttribute("categories", categories);
        model.addAttribute("activeCount", dashboardData.getActiveCategories());

        return "admin/dashboard";
    }

    // Category management
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new Category());

        return "admin/categories";
    }

    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute Category category, 
                                RedirectAttributes redirectAttributes) {
        categoryService.createCategory(category);
        redirectAttributes.addFlashAttribute("message", "Category created successfully!");

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Integer id, 
                                  Model model) {
        Optional<Category> categoryOpt = categoryService.findCategoryById(id);
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid category ID: " + id);
        }

        model.addAttribute("category", categoryOpt.get());

        return "admin/edit-category";
    }

    @PostMapping("/categories/update")
    public String updateCategory(@ModelAttribute Category category, 
                                RedirectAttributes redirectAttributes) {
        categoryService.updateCategory(category);
        redirectAttributes.addFlashAttribute("message", "Category updated successfully!");

        return "redirect:/admin/categories";
    }

    // Product management
    @GetMapping("/products")
    public String manageProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("newProduct", new Product());

        return "admin/products";
    }

    @PostMapping("/products/create")
    public String createProduct(@ModelAttribute Product product, 
                               @RequestParam Integer categoryId,
                               RedirectAttributes redirectAttributes) {
        productService.createProduct(product, categoryId);
        redirectAttributes.addFlashAttribute("message", "Product created successfully!");

        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Integer id, 
                                 Model model) {
        Optional<Product> productOpt = productService.findProductById(id);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }

        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", categories);

        return "admin/edit-product";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product, 
                               @RequestParam Integer categoryId,
                               RedirectAttributes redirectAttributes) {
        productService.updateProduct(product, categoryId);
        redirectAttributes.addFlashAttribute("message", "Product updated successfully!");

        return "redirect:/admin/products";
    }
}
