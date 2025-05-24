package com.project.demo.service;


import jakarta.servlet.http.HttpSession;


public interface AdminService {
    

    boolean isAdmin(HttpSession session);
    

    AdminDashboardData getDashboardData();
    

    class AdminDashboardData {
        private long activeCategories;
        
        public AdminDashboardData(long activeCategories) {
            this.activeCategories = activeCategories;
        }
        
        public long getActiveCategories() {
            return activeCategories;
        }
        
        public void setActiveCategories(long activeCategories) {
            this.activeCategories = activeCategories;
        }
    }
}