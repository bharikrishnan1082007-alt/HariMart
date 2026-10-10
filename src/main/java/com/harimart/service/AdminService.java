package com.harimart.service;

import com.harimart.dao.*;
import com.harimart.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AdminService {

    private final UserDAO userDAO = new UserDAO();
    private final SellerDAO sellerDAO = new SellerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    public AnalyticsSummary getPlatformSummary() {
        AnalyticsSummary summary = new AnalyticsSummary();
        summary.setTotalUsers(userDAO.countAll());
        summary.setTotalSellers(sellerDAO.countAll());
        summary.setTotalProducts(productDAO.countAll());
        summary.setTotalOrders(orderDAO.countAll());

        BigDecimal totalRevenue = orderDAO.calculateTotalPlatformRevenue();
        summary.setTotalRevenue(totalRevenue);

        // Platform 10% commission
        BigDecimal totalCommission = totalRevenue.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        summary.setTotalCommission(totalCommission);

        summary.setLowStockProducts(productDAO.countLowStock(5));
        return summary;
    }

    public List<User> getAllUsers(String search) {
        return userDAO.findAll(search);
    }

    public boolean updateUserRole(Long userId, String role) {
        return userDAO.updateRole(userId, role);
    }

    public boolean updateUserStatus(Long userId, String status) {
        return userDAO.updateStatus(userId, status);
    }

    public List<Seller> getAllSellers() {
        return sellerDAO.findAll();
    }

    public boolean updateSellerStatus(Long sellerId, String status) {
        boolean ok = sellerDAO.updateStatus(sellerId, status);
        if (ok && "APPROVED".equalsIgnoreCase(status)) {
            Seller s = sellerDAO.findById(sellerId);
            if (s != null) {
                userDAO.updateRole(s.getUserId(), "SELLER");
            }
        }
        return ok;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Category saveCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
            category.setSlug(category.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }
        if (category.getId() != null && category.getId() > 0) {
            categoryDAO.update(category);
            return categoryDAO.findById(category.getId());
        } else {
            return categoryDAO.create(category);
        }
    }

    public boolean deleteCategory(Long id) {
        return categoryDAO.delete(id);
    }

    public List<Product> getAllProducts() {
        return productDAO.search(null, null, null, null, null, null, "newest", 0, 100);
    }

    public boolean toggleProductActive(Long productId) {
        return productDAO.toggleActive(productId);
    }

    public List<Order> getAllOrders(int limit) {
        return orderDAO.findAll(limit);
    }

    public boolean updateOrderStatus(Long orderId, String status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }
}
