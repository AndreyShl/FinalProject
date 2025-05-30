package com.project.demo.service.impl;

import com.project.demo.model.entity.Basket;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import com.project.demo.model.repository.BasketRepository;
import com.project.demo.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Override
    public List<Basket> getBasketItemsByUser(User user) {
        return basketRepository.findByUser(user);
    }

    @Override
    public Basket addProductToBasket(User user, Product product, BigDecimal amount) throws IllegalArgumentException {
        // Check if the product is already in the basket
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            // Calculate the new total amount
            BigDecimal newTotalAmount = existingBasket.getAmount().add(amount);

            // Check if the new total amount exceeds available stock
            if (newTotalAmount.intValue() > product.getRemains()) {
                throw new IllegalArgumentException("Cannot add " + amount + " items. Only " + 
                    (product.getRemains() - existingBasket.getAmount().intValue()) + 
                    " more items available in stock.");
            }

            // Update the amount if the product is already in the basket
            existingBasket.setAmount(newTotalAmount);
            return basketRepository.save(existingBasket);
        } else {
            // Check if the requested amount exceeds available stock
            if (amount.intValue() > product.getRemains()) {
                throw new IllegalArgumentException("Cannot add " + amount + " items. Only " + 
                    product.getRemains() + " items available in stock.");
            }

            // Create a new basket item if the product is not in the basket
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            newBasket.setProduct(product);
            newBasket.setAmount(amount);
            return basketRepository.save(newBasket);
        }
    }

    @Override
    public void removeProductFromBasket(User user, Product product) {
        Basket basket = basketRepository.findByUserAndProduct(user, product);
        if (basket != null) {
            basketRepository.delete(basket);
        }
    }

    @Override
    public Basket updateProductAmount(User user, Product product, BigDecimal amount) throws IllegalArgumentException {
        Basket basket = basketRepository.findByUserAndProduct(user, product);
        if (basket != null) {
            // Check if the requested amount exceeds available stock
            if (amount.intValue() > product.getRemains()) {
                throw new IllegalArgumentException("Cannot update to " + amount + " items. Only " + 
                    product.getRemains() + " items available in stock.");
            }

            basket.setAmount(amount);
            return basketRepository.save(basket);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBasketTotal(User user) {
        List<Basket> basketItems = getBasketItemsByUser(user);
        return basketItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(item.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
