package com.harimart.dao;

import com.harimart.model.Address;
import com.harimart.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    public List<Address> findByUserId(Long userId) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, id DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
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

    public Address findById(Long id) {
        String sql = "SELECT * FROM addresses WHERE id = ?";
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

    public Address findDefaultByUserId(Long userId) {
        String sql = "SELECT * FROM addresses WHERE user_id = ? AND is_default = TRUE";
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
        // Fallback to latest address
        List<Address> all = findByUserId(userId);
        return all.isEmpty() ? null : all.get(0);
    }

    public Address create(Address address) {
        // If first address or marked default, reset other defaults
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            resetDefault(address.getUserId());
        }

        String sql = "INSERT INTO addresses (user_id, full_name, phone, address_line1, address_line2, city, state, pincode, country, is_default) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, address.getUserId());
            ps.setString(2, address.getFullName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getAddressLine1());
            ps.setString(5, address.getAddressLine2());
            ps.setString(6, address.getCity());
            ps.setString(7, address.getState());
            ps.setString(8, address.getPincode());
            ps.setString(9, address.getCountry() != null ? address.getCountry() : "India");
            ps.setBoolean(10, Boolean.TRUE.equals(address.getIsDefault()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        address.setId(keys.getLong(1));
                    }
                }
                return address;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setDefault(Long userId, Long addressId) {
        resetDefault(userId);
        String sql = "UPDATE addresses SET is_default = TRUE WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, addressId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long addressId, Long userId) {
        String sql = "DELETE FROM addresses WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, addressId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void resetDefault(Long userId) {
        String sql = "UPDATE addresses SET is_default = FALSE WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Address mapRow(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setFullName(rs.getString("full_name"));
        a.setPhone(rs.getString("phone"));
        a.setAddressLine1(rs.getString("address_line1"));
        a.setAddressLine2(rs.getString("address_line2"));
        a.setCity(rs.getString("city"));
        a.setState(rs.getString("state"));
        a.setPincode(rs.getString("pincode"));
        a.setCountry(rs.getString("country"));
        a.setIsDefault(rs.getBoolean("is_default"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
