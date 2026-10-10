package com.harimart.dao;

import com.harimart.model.Payment;
import com.harimart.util.DatabaseManager;

import java.sql.*;

public class PaymentDAO {

    public Payment create(Payment payment) {
        String sql = "INSERT INTO payments (order_id, transaction_ref, payment_method, amount, status, gateway_response) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, payment.getOrderId());
            ps.setString(2, payment.getTransactionRef());
            ps.setString(3, payment.getPaymentMethod());
            ps.setBigDecimal(4, payment.getAmount());
            ps.setString(5, payment.getStatus());
            ps.setString(6, payment.getGatewayResponse() != null ? payment.getGatewayResponse() : "SUCCESS");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setId(keys.getLong(1));
                    }
                }
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Payment findByOrderId(Long orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getLong("id"));
                    p.setOrderId(rs.getLong("order_id"));
                    p.setTransactionRef(rs.getString("transaction_ref"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setStatus(rs.getString("status"));
                    p.setGatewayResponse(rs.getString("gateway_response"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
