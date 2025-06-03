package com.project.demo.service;

import com.project.demo.model.entity.Favourite;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface FavouriteService {

    /**
     * Get all active favorites for a specific user
     * @param user the user whose favorites to retrieve
     * @return list of favorite items
     */
    List<Favourite> getFavouritesByUser(User user);

    /**
     * Add a product to the user's favorites
     * @param user the user
     * @param product the product to add to favorites
     * @return the created favorite item
     */
    Favourite addToFavourites(User user, Product product);

    /**
     * Remove a product from the user's favorites
     * @param user the user
     * @param product the product to remove from favorites
     */
    void removeFromFavourites(User user, Product product);

    /**
     * Check if a product is in the user's favorites
     * @param user the user
     * @param product the product to check
     * @return true if the product is in the user's favorites, false otherwise
     */
    boolean isProductInFavourites(User user, Product product);

    /**
     * Find a favorite by user and product
     * @param user the user
     * @param product the product
     * @return the favorite if found, empty otherwise
     */
    Optional<Favourite> findByUserAndProduct(User user, Product product);
}