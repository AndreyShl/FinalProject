package com.project.demo.model.repository;

import com.project.demo.model.entity.Category;
import com.project.demo.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
    

    List<Product> findByCategory(Category category);
    

    List<Product> findByNameContaining(String name);
}