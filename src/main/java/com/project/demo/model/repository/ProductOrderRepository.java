package com.project.demo.model.repository;

import com.project.demo.model.entity.Order;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.ProductOrder;
import com.project.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    
    List<ProductOrder> findByOrder(Order order);
    
    List<ProductOrder> findByProduct(Product product);
    
    List<ProductOrder> findByUser(User user);
    
    List<ProductOrder> findByOrderAndProduct(Order order, Product product);
}