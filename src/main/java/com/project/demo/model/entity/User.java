package com.project.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "users", schema = "shop")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "userName")
    private String username;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "status")
    private String status;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_holder")
    private String cardHolder;

    @Column(name = "card_expiry")
    private String cardExpiry;

    @Column(name = "card_cvv")
    private String cardCvv;

    public enum Role {
        ADMIN,
        USER
    }
}
