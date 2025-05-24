package com.project.demo.service.impl;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.model.repository.CategoryRepository;
import com.project.demo.model.repository.ProductsRepository;
import com.project.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productsRepository.findByCategory(category);
    }

    @Override
    public Optional<Product> findProductById(Integer id) {
        return productsRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productsRepository.save(product);
    }

    @Override
    public Product createProduct(Product product, Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
        product.setCategory(category);
        return productsRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product, Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
        product.setCategory(category);
        return productsRepository.save(product);
    }
}