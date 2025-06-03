package com.project.demo.service;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface ProductService {


    List<Product> getAllProducts();

    List<Product> getProductsByCategory(Category category);

    Optional<Product> findProductById(Integer id);

    Product saveProduct(Product product);

    Product createProduct(Product product, Integer categoryId);

    Product updateProduct(Product product, Integer categoryId);

    /**
     * Calculate the discounted price of a product
     * @param product the product
     * @return the discounted price, or the original price if no discount is applied
     */
    BigDecimal calculateDiscountedPrice(Product product);

    /**
     * Search for products by name (partial match)
     * @param query the search query
     * @return a list of products whose names contain the query
     */
    List<Product> searchProductsByName(String query);
}
