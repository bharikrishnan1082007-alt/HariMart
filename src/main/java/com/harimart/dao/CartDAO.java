package com.harimart.dao;

import com.harimart.model.CartItem;
import com.harimart.model.Product;
import com.harimart.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public List<CartItem> findItemsByUserOrSession(Long userId, String sessionId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT c.*, p.name AS prod_name, p.slug AS prod_slug, p.price AS prod_price, " +
                     "p.discount_price AS prod_discount, p.stock_quantity AS prod_stock, p.image_url AS prod_img, " +
                     "p.brand AS prod_brand, p.seller_id AS prod_seller, s.store_name AS seller_name " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "JOIN sellers s ON p.seller_id = s.id ";

        boolean hasUser = userId != null && userId > 0;
        if (hasUser) {
            sql += "WHERE c.user_id = ? ORDER BY c.id DESC";
        } else {
            sql += "WHERE c.session_id = ? AND c.user_id IS NULL ORDER BY c.id DESC";
        }

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasUser) {
                ps.setLong(1, userId);
            } else {
                ps.setString(1, sessionId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public CartItem addItem(Long userId, String sessionId, Long productId, int quantity) {
        // Check if item already exists in cart
        String findSql = (userId != null && userId > 0)
                ? "SELECT * FROM cart WHERE user_id = ? AND product_id = ?"
                : "SELECT * FROM cart WHERE session_id = ? AND user_id IS NULL AND product_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                if (userId != null && userId > 0) {
                    ps.setLong(1, userId);
                    ps.setLong(2, productId);
                } else {
                    ps.setString(1, sessionId);
                    ps.setLong(2, productId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long cartId = rs.getLong("id");
                        int newQty = rs.getInt("quantity") + quantity;
                        updateQuantity(cartId, newQty);
                        return findById(cartId);
                    }
                }
            }

            // Otherwise, insert new item
            String insertSql = "INSERT INTO cart (user_id, session_id, product_id, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                if (userId != null && userId > 0) {
                    ps.setLong(1, userId);
                } else {
                    ps.setNull(1, Types.BIGINT);
                }
                ps.setString(2, sessionId);
                ps.setLong(3, productId);
                ps.setInt(4, quantity);

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            return findById(keys.getLong(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CartItem findById(Long id) {
        String sql = "SELECT c.*, p.name AS prod_name, p.slug AS prod_slug, p.price AS prod_price, " +
                     "p.discount_price AS prod_discount, p.stock_quantity AS prod_stock, p.image_url AS prod_img, " +
                     "p.brand AS prod_brand, p.seller_id AS prod_seller, s.store_name AS seller_name " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            return removeItem(cartItemId);
        }
        String sql = "UPDATE cart SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeItem(Long cartItemId) {
        String sql = "DELETE FROM cart WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clearCart(Long userId, String sessionId) {
        String sql = (userId != null && userId > 0)
                ? "DELETE FROM cart WHERE user_id = ?"
                : "DELETE FROM cart WHERE session_id = ? AND user_id IS NULL";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null && userId > 0) {
                ps.setLong(1, userId);
            } else {
                ps.setString(1, sessionId);
            }
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void migrateGuestCart(String sessionId, Long userId) {
        if (sessionId == null || userId == null) return;
        String sql = "UPDATE cart SET user_id = ?, session_id = NULL WHERE session_id = ? AND user_id IS NULL";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCartCount(Long userId, String sessionId) {
        String sql = (userId != null && userId > 0)
                ? "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = ?"
                : "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE session_id = ? AND user_id IS NULL";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null && userId > 0) {
                ps.setLong(1, userId);
            } else {
                ps.setString(1, sessionId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        long uId = rs.getLong("user_id");
        if (!rs.wasNull()) item.setUserId(uId);
        item.setSessionId(rs.getString("session_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setUpdatedAt(rs.getTimestamp("updated_at"));

        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        p.setName(rs.getString("prod_name"));
        p.setSlug(rs.getString("prod_slug"));
        p.setPrice(rs.getBigDecimal("prod_price"));
        p.setDiscountPrice(rs.getBigDecimal("prod_discount"));
        p.setStockQuantity(rs.getInt("prod_stock"));
        p.setImageUrl(rs.getString("prod_img"));
        p.setBrand(rs.getString("prod_brand"));
        p.setSellerId(rs.getLong("prod_seller"));
        p.setSellerStoreName(rs.getString("seller_name"));
        item.setProduct(p);

        return item;
    }
}
