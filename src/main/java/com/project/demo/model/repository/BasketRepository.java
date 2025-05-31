package com.project.demo.model.repository;

import com.project.demo.model.entity.Basket;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
// - JPA provides the following methods:
// - save(T entity)
// - findById(ID id)
// - findAll()
// - delete(T entity)
// - deleteById(ID id)
// - existsById(ID id)

@Repository
public interface BasketRepository extends JpaRepository<Basket, Integer> {
    

    List<Basket> findByUser(User user);

    List<Basket> findByProduct(Product product);

    Basket findByUserAndProduct(User user, Product product);
}