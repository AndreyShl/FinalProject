package com.project.demo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    // - save(T entity)
    // - findById(ID id)
    // - findAll()
    // - delete(T entity)
    // - deleteById(ID id)
    // - existsById(ID id)
}