package com.project.demo.service.impl;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.model.repository.CategoryRepository;
import com.project.demo.model.repository.ProductsRepository;
import com.project.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Override
    public BigDecimal calculateDiscountedPrice(Product product) {
        if (product.getDiscount() == null || product.getDiscount().compareTo(BigDecimal.ZERO) <= 0) {
            return product.getPrice();
        }

        // Calculate discount amount
        BigDecimal discountPercentage = product.getDiscount().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal discountAmount = product.getPrice().multiply(discountPercentage);

        // Calculate discounted price
        return product.getPrice().subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<Product> searchProductsByName(String query) {
        return productsRepository.findByNameContaining(query);
    }
}
