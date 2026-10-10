package com.harimart.dao;

import com.harimart.model.Category;
import com.harimart.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public Category findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
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

    public Category findBySlug(String slug) {
        String sql = "SELECT * FROM categories WHERE LOWER(slug) = LOWER(?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slug.trim());
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

    public List<Category> findAllActive() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE status = 'ACTIVE' ORDER BY display_order ASC, name ASC";
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

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY display_order ASC, name ASC";
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

    public Category create(Category cat) {
        String sql = "INSERT INTO categories (name, slug, description, image_url, display_order, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getSlug());
            ps.setString(3, cat.getDescription());
            ps.setString(4, cat.getImageUrl());
            ps.setInt(5, cat.getDisplayOrder() != null ? cat.getDisplayOrder() : 0);
            ps.setString(6, cat.getStatus() != null ? cat.getStatus() : "ACTIVE");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        cat.setId(keys.getLong(1));
                    }
                }
                return cat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Category cat) {
        String sql = "UPDATE categories SET name = ?, slug = ?, description = ?, image_url = ?, display_order = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getSlug());
            ps.setString(3, cat.getDescription());
            ps.setString(4, cat.getImageUrl());
            ps.setInt(5, cat.getDisplayOrder());
            ps.setString(6, cat.getStatus());
            ps.setLong(7, cat.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setSlug(rs.getString("slug"));
        c.setDescription(rs.getString("description"));
        c.setImageUrl(rs.getString("image_url"));
        c.setDisplayOrder(rs.getInt("display_order"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
