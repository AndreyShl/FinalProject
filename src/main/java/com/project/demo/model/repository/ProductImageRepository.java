package com.project.demo.model.repository;

import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    
    List<ProductImage> findByProduct(Product product);
    
    ProductImage findByProductAndIsMainTrue(Product product);
    
    List<ProductImage> findByProductAndIdNot(Product product, Integer id);
}