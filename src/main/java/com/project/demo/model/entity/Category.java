package com.project.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "category", schema = "shop")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "categoryName")
    private String categoryName;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
