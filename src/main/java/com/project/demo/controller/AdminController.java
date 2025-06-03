package com.project.demo.controller;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Order;
import com.project.demo.model.entity.Product;
import com.project.demo.model.repository.OrdersRepository;
import com.project.demo.model.repository.ProductOrderRepository;
import com.project.demo.service.AdminService;
import com.project.demo.service.CategoryService;
import com.project.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

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
                               @RequestParam(value = "images", required = false) MultipartFile[] images,
                               RedirectAttributes redirectAttributes) {
        try {
            // Ensure images field is not bound directly
            product.setImages(null);

            // Create the product
            Product savedProduct = productService.createProduct(product, categoryId);

            redirectAttributes.addFlashAttribute("message", "Product created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating product: " + e.getMessage());
        }

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
                               @RequestParam(value = "newImages", required = false) MultipartFile[] newImages,
                               RedirectAttributes redirectAttributes) {
        try {
            // Ensure images field is not bound directly
            product.setImages(null);

            // Update the product
            Product updatedProduct = productService.updateProduct(product, categoryId);

            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Order management
    @GetMapping("/orders")
    public String manageOrders(Model model, 
                              @RequestParam(defaultValue = "createdAt") String sortBy,
                              @RequestParam(defaultValue = "desc") String sortDir) {
        // Create sort object based on parameters
        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        // Get all orders with sorting
        List<Order> orders = ordersRepository.findAll(sort);

        // For each order, ensure the productOrders are loaded
        for (Order order : orders) {
            if (order.getProductOrders() == null || order.getProductOrders().isEmpty()) {
                order.setProductOrders(productOrderRepository.findByOrder(order));
            }
        }

        // Add orders to the model
        model.addAttribute("orders", orders);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        // Define order status descriptions for display
        model.addAttribute("statusDescriptions", Map.of(
            "PENDING", "In Assembly",
            "SHIPPED", "In Delivery",
            "DELIVERED", "Delivered",
            "DELETED", "Deleted"
        ));

        return "admin/orders";
    }

    @PostMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable Integer id,
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes) {
        // Find the order by ID
        Optional<Order> orderOpt = ordersRepository.findById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Order not found");
            return "redirect:/admin/orders";
        }

        // Update the order status
        Order order = orderOpt.get();
        order.setOrderStatus(status);
        ordersRepository.save(order);

        redirectAttributes.addFlashAttribute("message", "Order status updated successfully!");

        return "redirect:/admin/orders";
    }

}
