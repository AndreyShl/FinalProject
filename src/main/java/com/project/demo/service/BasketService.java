package com.project.demo.service;

import com.project.demo.model.entity.Basket;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface BasketService {

    /**
     * Get all basket items for a specific user
     * @param user the user whose basket items to retrieve
     * @return list of basket items
     */
    List<Basket> getBasketItemsByUser(User user);

    /**
     * Add a product to the user's basket
     * @param user the user
     * @param product the product to add
     * @param amount the amount/quantity of the product
     * @return the created or updated basket item
     * @throws IllegalArgumentException if the requested amount exceeds available stock
     */
    Basket addProductToBasket(User user, Product product, BigDecimal amount) throws IllegalArgumentException;

    /**
     * Remove a product from the user's basket
     * @param user the user
     * @param product the product to remove
     */
    void removeProductFromBasket(User user, Product product);

    /**
     * Update the amount of a product in the user's basket
     * @param user the user
     * @param product the product to update
     * @param amount the new amount/quantity
     * @return the updated basket item
     * @throws IllegalArgumentException if the requested amount exceeds available stock
     */
    Basket updateProductAmount(User user, Product product, BigDecimal amount) throws IllegalArgumentException;

    /**
     * Calculate the total price of all items in the user's basket
     * @param user the user
     * @return the total price
     */
    BigDecimal calculateBasketTotal(User user);
}
