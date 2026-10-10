package com.harimart.dao;

import com.harimart.model.Product;
import com.harimart.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public Product findById(Long id) {
        String sql = "SELECT p.*, s.store_name AS seller_store_name, c.name AS category_name " +
                     "FROM products p " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.id = ?";
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

    public Product findBySlug(String slug) {
        String sql = "SELECT p.*, s.store_name AS seller_store_name, c.name AS category_name " +
                     "FROM products p " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE LOWER(p.slug) = LOWER(?)";
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

    public List<Product> findFeatured(int limit) {
        return findListByCondition("WHERE p.is_active = TRUE AND p.is_featured = TRUE ORDER BY p.id DESC LIMIT " + limit);
    }

    public List<Product> findNewArrivals(int limit) {
        return findListByCondition("WHERE p.is_active = TRUE ORDER BY p.id DESC LIMIT " + limit);
    }

    public List<Product> findBestsellers(int limit) {
        return findListByCondition("WHERE p.is_active = TRUE ORDER BY p.review_count DESC, p.rating DESC LIMIT " + limit);
    }

    public List<Product> findRelated(Long productId, Long categoryId, int limit) {
        String condition = "WHERE p.is_active = TRUE AND p.category_id = " + categoryId + 
                           " AND p.id != " + productId + " ORDER BY p.rating DESC LIMIT " + limit;
        return findListByCondition(condition);
    }

    public List<Product> findBySeller(Long sellerId) {
        return findListByCondition("WHERE p.seller_id = " + sellerId + " ORDER BY p.id DESC");
    }

    public List<Product> search(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, 
                               Double minRating, Boolean inStock, String sort, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT p.*, s.store_name AS seller_store_name, c.name AS category_name " +
                                              "FROM products p " +
                                              "JOIN sellers s ON p.seller_id = s.id " +
                                              "JOIN categories c ON p.category_id = c.id WHERE p.is_active = TRUE ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.brand) LIKE ?) ");
            String term = "%" + keyword.toLowerCase().trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND (CASE WHEN p.discount_price > 0 THEN p.discount_price ELSE p.price END) >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND (CASE WHEN p.discount_price > 0 THEN p.discount_price ELSE p.price END) <= ? ");
            params.add(maxPrice);
        }

        if (minRating != null && minRating > 0) {
            sql.append("AND p.rating >= ? ");
            params.add(minRating);
        }

        if (inStock != null && inStock) {
            sql.append("AND p.stock_quantity > 0 ");
        }

        // Sorting
        if ("price_asc".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY (CASE WHEN p.discount_price > 0 THEN p.discount_price ELSE p.price END) ASC ");
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY (CASE WHEN p.discount_price > 0 THEN p.discount_price ELSE p.price END) DESC ");
        } else if ("rating".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY p.rating DESC, p.review_count DESC ");
        } else if ("popular".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY p.review_count DESC ");
        } else {
            sql.append("ORDER BY p.id DESC ");
        }

        sql.append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public int countSearch(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, 
                           Double minRating, Boolean inStock) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products p WHERE p.is_active = TRUE ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.brand) LIKE ?) ");
            String term = "%" + keyword.toLowerCase().trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND (CASE WHEN p.discount_price > 0 THEN p.discount_price ELSE p.price END) >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND (CASE WHEN p.discount_price > 0 THEN p.discount_price ELSE p.price END) <= ? ");
            params.add(maxPrice);
        }

        if (minRating != null && minRating > 0) {
            sql.append("AND p.rating >= ? ");
            params.add(minRating);
        }

        if (inStock != null && inStock) {
            sql.append("AND p.stock_quantity > 0 ");
        }

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> autocomplete(String keyword, int limit) {
        List<String> suggestions = new ArrayList<>();
        String sql = "SELECT name FROM products WHERE is_active = TRUE AND LOWER(name) LIKE ? ORDER BY name ASC LIMIT ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase().trim() + "%");
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    public Product create(Product product) {
        String sql = "INSERT INTO products (seller_id, category_id, name, slug, description, price, discount_price, " +
                     "stock_quantity, brand, image_url, additional_images, is_featured, is_active, rating, review_count) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, product.getSellerId());
            ps.setLong(2, product.getCategoryId());
            ps.setString(3, product.getName());
            ps.setString(4, product.getSlug());
            ps.setString(5, product.getDescription());
            ps.setBigDecimal(6, product.getPrice());
            ps.setBigDecimal(7, product.getDiscountPrice() != null ? product.getDiscountPrice() : BigDecimal.ZERO);
            ps.setInt(8, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            ps.setString(9, product.getBrand());
            ps.setString(10, product.getImageUrl());
            ps.setString(11, product.getAdditionalImages() != null ? product.getAdditionalImages() : "");
            ps.setBoolean(12, product.getIsFeatured() != null ? product.getIsFeatured() : false);
            ps.setBoolean(13, product.getIsActive() != null ? product.getIsActive() : true);
            ps.setBigDecimal(14, product.getRating() != null ? product.getRating() : BigDecimal.valueOf(4.5));
            ps.setInt(15, product.getReviewCount() != null ? product.getReviewCount() : 0);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setId(keys.getLong(1));
                    }
                }
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id = ?, name = ?, slug = ?, description = ?, price = ?, discount_price = ?, " +
                     "stock_quantity = ?, brand = ?, image_url = ?, additional_images = ?, is_featured = ?, is_active = ? " +
                     "WHERE id = ? AND seller_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getSlug());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setBigDecimal(6, product.getDiscountPrice() != null ? product.getDiscountPrice() : BigDecimal.ZERO);
            ps.setInt(7, product.getStockQuantity());
            ps.setString(8, product.getBrand());
            ps.setString(9, product.getImageUrl());
            ps.setString(10, product.getAdditionalImages() != null ? product.getAdditionalImages() : "");
            ps.setBoolean(11, product.getIsFeatured());
            ps.setBoolean(12, product.getIsActive());
            ps.setLong(13, product.getId());
            ps.setLong(14, product.getSellerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateByAdmin(Product product) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, price = ?, discount_price = ?, " +
                     "stock_quantity = ?, brand = ?, image_url = ?, is_featured = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setBigDecimal(5, product.getDiscountPrice() != null ? product.getDiscountPrice() : BigDecimal.ZERO);
            ps.setInt(6, product.getStockQuantity());
            ps.setString(7, product.getBrand());
            ps.setString(8, product.getImageUrl());
            ps.setBoolean(9, product.getIsFeatured());
            ps.setBoolean(10, product.getIsActive());
            ps.setLong(11, product.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStockInTransaction(Long id, int quantityDelta, Connection conn) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ? AND (stock_quantity + ?) >= 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityDelta);
            ps.setLong(2, id);
            ps.setInt(3, quantityDelta);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActive(Long id) {
        String sql = "UPDATE products SET is_active = CASE WHEN is_active = TRUE THEN FALSE ELSE TRUE END WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long countLowStock(int threshold) {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity <= ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<Product> findListByCondition(String condition) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.store_name AS seller_store_name, c.name AS category_name " +
                     "FROM products p " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "JOIN categories c ON p.category_id = c.id " + condition;
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

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setName(rs.getString("name"));
        p.setSlug(rs.getString("slug"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setDiscountPrice(rs.getBigDecimal("discount_price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setBrand(rs.getString("brand"));
        p.setImageUrl(rs.getString("image_url"));
        p.setAdditionalImages(rs.getString("additional_images"));
        p.setIsFeatured(rs.getBoolean("is_featured"));
        p.setIsActive(rs.getBoolean("is_active"));
        p.setRating(rs.getBigDecimal("rating"));
        p.setReviewCount(rs.getInt("review_count"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            p.setSellerStoreName(rs.getString("seller_store_name"));
        } catch (SQLException ignored) {}
        try {
            p.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {}
        return p;
    }
}
