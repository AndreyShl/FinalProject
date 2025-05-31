package com.project.demo.model.repository;

import com.project.demo.model.entity.Favourite;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {
    

    List<Favourite> findByUser(User user);

    List<Favourite> findByProduct(Product product);

    List<Favourite> findByUserAndStatus(User user, String status);
}