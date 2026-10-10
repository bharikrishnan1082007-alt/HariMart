package com.harimart.dao;

import com.harimart.model.Product;
import com.harimart.model.Wishlist;
import com.harimart.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    public List<Wishlist> findByUserId(Long userId) {
        List<Wishlist> list = new ArrayList<>();
        String sql = "SELECT w.*, p.name AS prod_name, p.slug AS prod_slug, p.price AS prod_price, " +
                     "p.discount_price AS prod_discount, p.stock_quantity AS prod_stock, p.image_url AS prod_img, " +
                     "p.brand AS prod_brand, s.store_name AS seller_name " +
                     "FROM wishlist w " +
                     "JOIN products p ON w.product_id = p.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE w.user_id = ? ORDER BY w.id DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Wishlist w = new Wishlist();
                    w.setId(rs.getLong("id"));
                    w.setUserId(rs.getLong("user_id"));
                    w.setProductId(rs.getLong("product_id"));
                    w.setCreatedAt(rs.getTimestamp("created_at"));

                    Product p = new Product();
                    p.setId(rs.getLong("product_id"));
                    p.setName(rs.getString("prod_name"));
                    p.setSlug(rs.getString("prod_slug"));
                    p.setPrice(rs.getBigDecimal("prod_price"));
                    p.setDiscountPrice(rs.getBigDecimal("prod_discount"));
                    p.setStockQuantity(rs.getInt("prod_stock"));
                    p.setImageUrl(rs.getString("prod_img"));
                    p.setBrand(rs.getString("prod_brand"));
                    p.setSellerStoreName(rs.getString("seller_name"));
                    w.setProduct(p);

                    list.add(w);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isInWishlist(Long userId, Long productId) {
        String sql = "SELECT 1 FROM wishlist WHERE user_id = ? AND product_id = ?";
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

    public boolean toggle(Long userId, Long productId) {
        if (isInWishlist(userId, productId)) {
            remove(userId, productId);
            return false; // Removed
        } else {
            String sql = "INSERT INTO wishlist (user_id, product_id) VALUES (?, ?)";
            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, userId);
                ps.setLong(2, productId);
                ps.executeUpdate();
                return true; // Added
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean remove(Long userId, Long productId) {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
