package com.project.demo.model.repository;

import com.project.demo.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT count(c) FROM Category c WHERE c.status = 'ACTIVE'")
    long countActiveCategories();


    Category findByCategoryName(String name);


    List<Category> findByStatus(String status);
}