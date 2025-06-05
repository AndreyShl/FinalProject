package com.project.demo;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import com.project.demo.model.entity.Basket;
import com.project.demo.model.repository.CategoryRepository;
import com.project.demo.model.repository.ProductsRepository;
import com.project.demo.model.repository.BasketRepository;
import com.project.demo.service.BasketService;
import com.project.demo.service.ProductService;
import com.project.demo.service.impl.BasketServiceImpl;
import com.project.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductService and BasketService focusing on:
 * 1. Product creation
 * 2. Adding products to basket
 * 3. Stock management (remains)
 * 
 * These tests verify that:
 * - Products can be created with valid categories
 * - Products cannot be created with invalid categories
 * - Discounted prices are calculated correctly
 * - Products can be added to basket if sufficient stock exists
 * - Products cannot be added to basket if stock is insufficient
 * - Basket items cannot be updated to quantities exceeding available stock
 */
public class ProductServiceTest {

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @InjectMocks
    private BasketServiceImpl basketService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // We need to manually wire the productService into basketService
        // since @InjectMocks doesn't handle dependencies between mocked objects
        ReflectionTestUtils.setField(basketService, "productService", productService);
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        Integer categoryId = 1;
        String productName = "Test Coffee";
        BigDecimal price = new BigDecimal("10.99");
        Integer remains = 50;

        Category category = new Category();
        category.setId(categoryId);
        category.setCategoryName("Coffee");
        category.setStatus("ACTIVE");

        Product product = new Product();
        product.setName(productName);
        product.setPrice(price);
        product.setRemains(remains);
        product.setDescription("Delicious test coffee");

        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setName(productName);
        savedProduct.setPrice(price);
        savedProduct.setRemains(remains);
        savedProduct.setDescription("Delicious test coffee");
        savedProduct.setCategory(category);

        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productsRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product createdProduct = productService.createProduct(product, categoryId);

        // Assert
        assertNotNull(createdProduct);
        assertEquals(1, createdProduct.getId());
        assertEquals(productName, createdProduct.getName());
        assertEquals(price, createdProduct.getPrice());
        assertEquals(remains, createdProduct.getRemains());
        assertEquals(category, createdProduct.getCategory());

        // Verify interactions
        verify(categoryRepository).findById(categoryId);
        verify(productsRepository).save(any(Product.class));
    }

    @Test
    public void testCreateProductWithInvalidCategory() {
        // Arrange
        Integer invalidCategoryId = 999;
        Product product = new Product();
        product.setName("Test Coffee");
        product.setPrice(new BigDecimal("10.99"));
        product.setRemains(50);

        // Mock repository behavior
        when(categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(product, invalidCategoryId);
        });

        assertTrue(exception.getMessage().contains("Invalid category ID"));

        // Verify interactions
        verify(categoryRepository).findById(invalidCategoryId);
        verify(productsRepository, never()).save(any(Product.class));
    }

    @Test
    public void testCalculateDiscountedPrice() {
        // Arrange
        Product product = new Product();
        product.setPrice(new BigDecimal("100.00"));
        product.setDiscount(new BigDecimal("20")); // 20% discount

        // Expected discounted price: 100 - (100 * 0.2) = 80
        BigDecimal expectedPrice = new BigDecimal("80.00");

        // Act
        BigDecimal discountedPrice = productService.calculateDiscountedPrice(product);

        // Assert
        assertEquals(expectedPrice, discountedPrice);
    }

    @Test
    public void testCalculateDiscountedPriceWithNoDiscount() {
        // Arrange
        Product product = new Product();
        product.setPrice(new BigDecimal("100.00"));
        product.setDiscount(null);

        // Expected price is the original price
        BigDecimal expectedPrice = new BigDecimal("100.00");

        // Act
        BigDecimal discountedPrice = productService.calculateDiscountedPrice(product);

        // Assert
        assertEquals(expectedPrice, discountedPrice);
    }

    @Test
    public void testAddProductToBasket() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");

        Category category = new Category();
        category.setId(1);
        category.setCategoryName("Coffee");

        Product product = new Product();
        product.setId(1);
        product.setName("Test Coffee");
        product.setPrice(new BigDecimal("10.99"));
        product.setRemains(50); // 50 items in stock
        product.setCategory(category);

        BigDecimal amount = new BigDecimal("2"); // Adding 2 items to basket

        Basket savedBasket = new Basket();
        savedBasket.setId(1);
        savedBasket.setUser(user);
        savedBasket.setProduct(product);
        savedBasket.setAmount(amount);

        // Mock repository behavior
        when(basketRepository.findByUserAndProduct(user, product)).thenReturn(null); // Product not in basket yet
        when(basketRepository.save(any(Basket.class))).thenReturn(savedBasket);

        // Act
        Basket result = basketService.addProductToBasket(user, product, amount);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(product, result.getProduct());
        assertEquals(amount, result.getAmount());

        // Verify interactions
        verify(basketRepository).findByUserAndProduct(user, product);
        verify(basketRepository).save(any(Basket.class));
    }

    @Test
    public void testAddProductToBasketExceedingStock() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");

        Product product = new Product();
        product.setId(1);
        product.setName("Test Coffee");
        product.setPrice(new BigDecimal("10.99"));
        product.setRemains(5); // Only 5 items in stock

        BigDecimal amount = new BigDecimal("10"); // Trying to add 10 items to basket

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            basketService.addProductToBasket(user, product, amount);
        });

        assertTrue(exception.getMessage().contains("Cannot add"));
        assertTrue(exception.getMessage().contains("items available in stock"));

        // Verify interactions
        verify(basketRepository).findByUserAndProduct(user, product);
        verify(basketRepository, never()).save(any(Basket.class));
    }

    @Test
    public void testUpdateBasketItemExceedingStock() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");

        Product product = new Product();
        product.setId(1);
        product.setName("Test Coffee");
        product.setPrice(new BigDecimal("10.99"));
        product.setRemains(5); // Only 5 items in stock

        Basket existingBasket = new Basket();
        existingBasket.setId(1);
        existingBasket.setUser(user);
        existingBasket.setProduct(product);
        existingBasket.setAmount(new BigDecimal("2")); // Already has 2 items

        BigDecimal newAmount = new BigDecimal("10"); // Trying to update to 10 items

        // Mock repository behavior
        when(basketRepository.findByUserAndProduct(user, product)).thenReturn(existingBasket);

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            basketService.updateProductAmount(user, product, newAmount);
        });

        assertTrue(exception.getMessage().contains("Cannot update"));
        assertTrue(exception.getMessage().contains("items available in stock"));

        // Verify interactions
        verify(basketRepository).findByUserAndProduct(user, product);
        verify(basketRepository, never()).save(any(Basket.class));
    }
}
