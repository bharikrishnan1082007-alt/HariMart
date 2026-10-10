package com.harimart.dao;

import com.harimart.model.Seller;
import com.harimart.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDAO {

    public Seller findById(Long id) {
        String sql = "SELECT s.*, u.name AS owner_name FROM sellers s JOIN users u ON s.user_id = u.id WHERE s.id = ?";
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

    public Seller findByUserId(Long userId) {
        String sql = "SELECT s.*, u.name AS owner_name FROM sellers s JOIN users u ON s.user_id = u.id WHERE s.user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
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

    public Seller create(Seller seller) {
        String sql = "INSERT INTO sellers (user_id, store_name, business_email, phone, address, tax_id, status, commission_rate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, seller.getUserId());
            ps.setString(2, seller.getStoreName());
            ps.setString(3, seller.getBusinessEmail());
            ps.setString(4, seller.getPhone());
            ps.setString(5, seller.getAddress());
            ps.setString(6, seller.getTaxId());
            ps.setString(7, seller.getStatus() != null ? seller.getStatus() : "PENDING");
            ps.setBigDecimal(8, seller.getCommissionRate() != null ? seller.getCommissionRate() : BigDecimal.valueOf(10.0));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        seller.setId(keys.getLong(1));
                    }
                }
                return seller;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(Long sellerId, String status) {
        String sql = "UPDATE sellers SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, sellerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Seller> findAll() {
        List<Seller> list = new ArrayList<>();
        String sql = "SELECT s.*, u.name AS owner_name FROM sellers s JOIN users u ON s.user_id = u.id ORDER BY s.id DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM sellers";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Seller mapRow(ResultSet rs) throws SQLException {
        Seller s = new Seller();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setStoreName(rs.getString("store_name"));
        s.setBusinessEmail(rs.getString("business_email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        s.setTaxId(rs.getString("tax_id"));
        s.setStatus(rs.getString("status"));
        s.setCommissionRate(rs.getBigDecimal("commission_rate"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            s.setOwnerName(rs.getString("owner_name"));
        } catch (SQLException ignored) {}
        return s;
    }
}
