package com.project.demo.model.repository;

import com.project.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);
}