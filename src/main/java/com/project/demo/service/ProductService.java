package com.project.demo.service;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import java.util.List;
import java.util.Optional;


public interface ProductService {
    

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(Category category);

    Optional<Product> findProductById(Integer id);

    Product saveProduct(Product product);

    Product createProduct(Product product, Integer categoryId);

    Product updateProduct(Product product, Integer categoryId);
}