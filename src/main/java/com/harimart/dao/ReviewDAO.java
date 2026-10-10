package com.harimart.dao;

import com.harimart.model.Review;
import com.harimart.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public List<Review> findByProductId(Long productId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS user_name, p.name AS prod_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN products p ON r.product_id = p.id " +
                     "WHERE r.product_id = ? ORDER BY r.id DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setTitle(rs.getString("title"));
                    r.setComment(rs.getString("comment"));
                    r.setIsVerifiedPurchase(rs.getBoolean("is_verified_purchase"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    r.setProductName(rs.getString("prod_name"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean create(Review review) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, title, comment, is_verified_purchase) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, review.getProductId());
                ps.setLong(2, review.getUserId());
                ps.setInt(3, review.getRating());
                ps.setString(4, review.getTitle());
                ps.setString(5, review.getComment());
                ps.setBoolean(6, Boolean.TRUE.equals(review.getIsVerifiedPurchase()));
                ps.executeUpdate();
            }

            // Recalculate average rating & review count for product
            String updateProd = "UPDATE products SET " +
                                "rating = (SELECT ROUND(AVG(rating), 2) FROM reviews WHERE product_id = ?), " +
                                "review_count = (SELECT COUNT(*) FROM reviews WHERE product_id = ?) " +
                                "WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateProd)) {
                ps.setLong(1, review.getProductId());
                ps.setLong(2, review.getProductId());
                ps.setLong(3, review.getProductId());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasReviewed(Long userId, Long productId) {
        String sql = "SELECT 1 FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPurchased(Long userId, Long productId) {
        String sql = "SELECT 1 FROM order_items oi JOIN orders o ON oi.order_id = o.id " +
                     "WHERE o.user_id = ? AND oi.product_id = ? AND o.order_status != 'CANCELLED'";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
