package com.project.demo.service.impl;

import com.project.demo.model.entity.User;
import com.project.demo.model.repository.CategoryRepository;
import com.project.demo.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    @Override
    public AdminDashboardData getDashboardData() {
        long activeCategories = categoryRepository.countActiveCategories();
        return new AdminDashboardData(activeCategories);
    }
}