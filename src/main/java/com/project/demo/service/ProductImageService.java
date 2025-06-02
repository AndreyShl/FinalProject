package com.project.demo.service;

import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {
    
    /**
     * Save images for a product
     * @param product The product to save images for
     * @param images The image files to save
     * @return List of saved ProductImage entities
     */
    List<ProductImage> saveProductImages(Product product, MultipartFile[] images) throws IOException;
    
    /**
     * Get all images for a product
     * @param product The product to get images for
     * @return List of ProductImage entities
     */
    List<ProductImage> getProductImagesByProduct(Product product);
    
    /**
     * Get the main image for a product
     * @param product The product to get the main image for
     * @return The main ProductImage entity, or null if none exists
     */
    ProductImage getMainProductImage(Product product);
    
    /**
     * Delete a product image
     * @param imageId The ID of the image to delete
     * @return true if successful, false otherwise
     */
    boolean deleteProductImage(Integer imageId);
    
    /**
     * Set a product image as the main image
     * @param imageId The ID of the image to set as main
     * @return true if successful, false otherwise
     */
    boolean setMainProductImage(Integer imageId);
    
    /**
     * Get a product image by ID
     * @param imageId The ID of the image to get
     * @return The ProductImage entity, or null if none exists
     */
    ProductImage getProductImage(Integer imageId);
}