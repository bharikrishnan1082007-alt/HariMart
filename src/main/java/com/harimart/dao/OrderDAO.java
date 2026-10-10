package com.harimart.dao;

import com.harimart.model.Address;
import com.harimart.model.Order;
import com.harimart.model.OrderItem;
import com.harimart.model.User;
import com.harimart.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public Order createOrder(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO orders (order_number, user_id, address_id, subtotal, discount, tax, shipping_fee, " +
                     "total_amount, payment_method, payment_status, order_status, tracking_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderNumber());
            ps.setLong(2, order.getUserId());
            ps.setLong(3, order.getAddressId());
            ps.setBigDecimal(4, order.getSubtotal());
            ps.setBigDecimal(5, order.getDiscount());
            ps.setBigDecimal(6, order.getTax());
            ps.setBigDecimal(7, order.getShippingFee());
            ps.setBigDecimal(8, order.getTotalAmount());
            ps.setString(9, order.getPaymentMethod());
            ps.setString(10, order.getPaymentStatus());
            ps.setString(11, order.getOrderStatus());
            ps.setString(12, order.getTrackingNumber());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
        }
        return order;
    }

    public OrderItem createOrderItem(OrderItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, seller_id, quantity, unit_price, total_price, fulfillment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setLong(3, item.getSellerId());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getUnitPrice());
            ps.setBigDecimal(6, item.getTotalPrice());
            ps.setString(7, item.getFulfillmentStatus());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
        }
        return item;
    }

    public Order findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    attachOrderDetails(order, conn);
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Order findByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM orders WHERE order_number = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    attachOrderDetails(order, conn);
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> findByUserId(Long userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    attachOrderDetails(order, conn);
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderItem> findOrderItems(Long orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS prod_name, p.image_url AS prod_img, s.store_name AS seller_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN sellers s ON oi.seller_id = s.id " +
                     "WHERE oi.order_id = ? ORDER BY oi.id ASC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapOrderItemRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<OrderItem> findOrderItemsBySeller(Long sellerId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS prod_name, p.image_url AS prod_img, s.store_name AS seller_name, o.order_number " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN sellers s ON oi.seller_id = s.id " +
                     "JOIN orders o ON oi.order_id = o.id " +
                     "WHERE oi.seller_id = ? ORDER BY oi.id DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = mapOrderItemRow(rs);
                    item.setOrderNumber(rs.getString("order_number"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET order_status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrderItemStatus(Long orderItemId, Long sellerId, String status) {
        String sql = "UPDATE order_items SET fulfillment_status = ? WHERE id = ? AND seller_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, orderItemId);
            ps.setLong(3, sellerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Order> findAll(int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    attachOrderDetails(order, conn);
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public BigDecimal calculateTotalPlatformRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE order_status != 'CANCELLED'";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateSellerGrossRevenue(Long sellerId) {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM order_items WHERE seller_id = ? AND fulfillment_status != 'CANCELLED'";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private void attachOrderDetails(Order order, Connection conn) throws SQLException {
        // Attach address
        if (order.getAddressId() != null) {
            String addrSql = "SELECT * FROM addresses WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(addrSql)) {
                ps.setLong(1, order.getAddressId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Address a = new Address();
                        a.setId(rs.getLong("id"));
                        a.setFullName(rs.getString("full_name"));
                        a.setPhone(rs.getString("phone"));
                        a.setAddressLine1(rs.getString("address_line1"));
                        a.setAddressLine2(rs.getString("address_line2"));
                        a.setCity(rs.getString("city"));
                        a.setState(rs.getString("state"));
                        a.setPincode(rs.getString("pincode"));
                        order.setShippingAddress(a);
                    }
                }
            }
        }

        // Attach buyer
        String userSql = "SELECT name, email, phone FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setLong(1, order.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(order.getUserId());
                    u.setName(rs.getString("name"));
                    u.setEmail(rs.getString("email"));
                    u.setPhone(rs.getString("phone"));
                    order.setBuyer(u);
                }
            }
        }

        // Attach items
        order.setItems(findOrderItems(order.getId()));
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setUserId(rs.getLong("user_id"));
        long addrId = rs.getLong("address_id");
        if (!rs.wasNull()) o.setAddressId(addrId);
        o.setSubtotal(rs.getBigDecimal("subtotal"));
        o.setDiscount(rs.getBigDecimal("discount"));
        o.setTax(rs.getBigDecimal("tax"));
        o.setShippingFee(rs.getBigDecimal("shipping_fee"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setOrderStatus(rs.getString("order_status"));
        o.setTrackingNumber(rs.getString("tracking_number"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        return o;
    }

    private OrderItem mapOrderItemRow(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setSellerId(rs.getLong("seller_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setTotalPrice(rs.getBigDecimal("total_price"));
        item.setFulfillmentStatus(rs.getString("fulfillment_status"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            item.setProductName(rs.getString("prod_name"));
        } catch (SQLException ignored) {}
        try {
            item.setProductImageUrl(rs.getString("prod_img"));
        } catch (SQLException ignored) {}
        try {
            item.setSellerStoreName(rs.getString("seller_name"));
        } catch (SQLException ignored) {}
        return item;
    }
}
