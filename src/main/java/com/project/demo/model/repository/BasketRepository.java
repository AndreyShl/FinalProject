package com.project.demo.model.repository;

import com.project.demo.model.entity.Basket;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BasketRepository extends BaseRepository<Basket, Integer> {
    

    List<Basket> findByUser(User user);

    List<Basket> findByProduct(Product product);

    Basket findByUserAndProduct(User user, Product product);
}