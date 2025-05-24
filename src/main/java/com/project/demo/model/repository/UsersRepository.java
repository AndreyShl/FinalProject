package com.project.demo.model.repository;

import com.project.demo.model.entity.User;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersRepository extends BaseRepository<User, Integer> {
    User findByLogin(String login);
}