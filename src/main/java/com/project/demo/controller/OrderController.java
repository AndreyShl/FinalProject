package com.project.demo.controller;

import com.project.demo.model.entity.*;
import com.project.demo.model.repository.OrdersRepository;
import com.project.demo.model.repository.ProductOrderRepository;
import com.project.demo.service.BasketService;
import com.project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private BasketService basketService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @GetMapping("/create")
    public String showCreateOrderForm(Model model, HttpSession session) {
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

        // Get basket items and total
        List<Basket> basketItems = basketService.getBasketItemsByUser(user);
        if (basketItems.isEmpty()) {
            return "redirect:/basket";
        }

        BigDecimal total = basketService.calculateBasketTotal(user);

        // Create payment methods list
        List<String> paymentMethods = new java.util.ArrayList<>(List.of("Credit Card", "PayPal", "Cash on Delivery"));

        // Check if user has a linked card and add it to payment methods
        if (userService.hasPaymentCard(user)) {
            String lastFourDigits = user.getCardNumber().substring(user.getCardNumber().length() - 4);
            String cardOption = "My Card (**** **** **** " + lastFourDigits + ")";
            // Add at the beginning of the list
            paymentMethods.add(0, cardOption);
        }

        // Add data to model
        model.addAttribute("basketItems", basketItems);
        model.addAttribute("total", total);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("user", user);

        return "orders/create";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam("paymentMethod") String paymentMethod, HttpSession session, Model model) {
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

        // Get basket items
        List<Basket> basketItems = basketService.getBasketItemsByUser(user);
        if (basketItems.isEmpty()) {
            return "redirect:/basket";
        }

        // Calculate total price for the order
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Basket basketItem : basketItems) {
            totalPrice = totalPrice.add(basketItem.getProduct().getPrice().multiply(basketItem.getAmount()));
        }

        try {
            // Списываем средства с баланса пользователя
            user = userService.deductFromBalance(user, totalPrice);

            // Create a single order for all basket items
            Order order = new Order();
            order.setUser(user);
            order.setOrderStatus("PENDING");
            order.setPaymentMethod(paymentMethod);
            order.setPrice(totalPrice);

            // Initialize the productOrders list
            order.setProductOrders(new ArrayList<>());

            // Save the order to get an ID
            order = ordersRepository.save(order);

            // Create ProductOrder entries for each basket item
            for (Basket basketItem : basketItems) {
                ProductOrder productOrder = new ProductOrder();
                productOrder.setUser(user);
                productOrder.setProduct(basketItem.getProduct());
                productOrder.setOrder(order);
                productOrder.setQuantity(basketItem.getAmount());
                productOrder.setPrice(basketItem.getProduct().getPrice().multiply(basketItem.getAmount()));

                // Save the product order
                productOrderRepository.save(productOrder);

                // Add to the order's list of product orders
                order.getProductOrders().add(productOrder);

                // Remove the item from the basket
                basketService.removeProductFromBasket(user, basketItem.getProduct());
            }

            // Update the order with the product orders
            ordersRepository.save(order);

            // Redirect to a confirmation page
            return "redirect:/orders/confirmation";

        } catch (UserService.InsufficientBalanceException e) {
            // Если на балансе недостаточно средств, возвращаем пользователя на страницу корзины с сообщением об ошибке
            model.addAttribute("basketItems", basketItems);
            model.addAttribute("total", totalPrice);
            model.addAttribute("errorMessage", "Недостаточно средств на балансе. Текущий баланс: " + user.getBalance() + ", Требуется: " + totalPrice);
            return "products/basket";
        }
    }

    @GetMapping("/confirmation")
    public String showConfirmation() {
        return "orders/confirmation";
    }

    @GetMapping("")
    public String showUserOrders(Model model) {
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

        // Get all orders for the user
        List<Order> orders = ordersRepository.findByUser(user);

        // Filter out orders with "DELETED" status
        orders.removeIf(order -> "DELETED".equals(order.getOrderStatus()));

        // For each order, ensure the productOrders are loaded
        for (Order order : orders) {
            if (order.getProductOrders() == null || order.getProductOrders().isEmpty()) {
                List<ProductOrder> productOrders = productOrderRepository.findByOrder(order);
                order.setProductOrders(productOrders);
            }
        }

        // Add orders to the model
        model.addAttribute("orders", orders);

        // Define order status descriptions for display
        model.addAttribute("statusDescriptions", Map.of(
            "PENDING", "In Assembly",
            "SHIPPED", "In Delivery",
            "DELIVERED", "Delivered",
            "DELETED", "Deleted"
        ));

        return "orders/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer orderId) {
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

        // Find the order by ID
        Order order = ordersRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/orders";
        }

        // Check if the order belongs to the authenticated user
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders";
        }

        // Check if the order is in "SHIPPED" or "DELIVERED" status
        if ("SHIPPED".equals(order.getOrderStatus()) || "DELIVERED".equals(order.getOrderStatus())) {
            // If so, only allow deletion if the user has ADMIN role
            if (user.getRole() != User.Role.ADMIN) {
                return "redirect:/orders";
            }
        }

        // Update the order status to "DELETED" instead of actually deleting it
        order.setOrderStatus("DELETED");
        ordersRepository.save(order);

        return "redirect:/orders";
    }
}
