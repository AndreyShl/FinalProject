package com.project.demo.service;

import com.project.demo.model.entity.Category;
import java.util.List;
import java.util.Optional;


public interface CategoryService {
    

    List<Category> getAllCategories();
    

    Optional<Category> findCategoryById(Integer id);
    

    Category saveCategory(Category category);
    

    Category createCategory(Category category);
    



    Category updateCategory(Category category);
    

    long countActiveCategories();
}