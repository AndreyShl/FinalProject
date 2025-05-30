package com.project.demo.model.repository;

import com.project.demo.model.entity.Order;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrdersRepository extends BaseRepository<Order, Integer> {
    

    List<Order> findByUser(User user);


    List<Order> findByProductOrders_Product(Product product);



    List<Order> findByOrderStatus(String orderStatus);
    

    List<Order> findByUserAndOrderStatus(User user, String orderStatus);
}