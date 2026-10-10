package com.harimart.service;

import com.harimart.dao.CartDAO;
import com.harimart.dao.OrderDAO;
import com.harimart.dao.PaymentDAO;
import com.harimart.dao.ProductDAO;
import com.harimart.model.CartItem;
import com.harimart.model.Order;
import com.harimart.model.OrderItem;
import com.harimart.model.Payment;
import com.harimart.model.Product;
import com.harimart.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final CartService cartService = new CartService();

    public Order placeOrder(Long userId, Long addressId, String paymentMethod, String couponCode) throws Exception {
        if (userId == null || addressId == null || paymentMethod == null) {
            throw new IllegalArgumentException("Invalid checkout parameters.");
        }

        List<CartItem> cartItems = cartDAO.findItemsByUserOrSession(userId, null);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        // Validate stock beforehand
        for (CartItem item : cartItems) {
            Product p = productDAO.findById(item.getProductId());
            if (p == null || !Boolean.TRUE.equals(p.getIsActive())) {
                throw new IllegalStateException("Product '" + (p != null ? p.getName() : "Item") + "' is no longer available.");
            }
            if (p.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for '" + p.getName() + "'. Available: " + p.getStockQuantity());
            }
        }

        CartService.CartTotals totals = cartService.calculateTotals(cartItems, couponCode);
        String orderNumber = generateOrderNumber();
        String trackingNumber = "HMTRK" + System.currentTimeMillis();

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setSubtotal(totals.getSubtotal());
        order.setDiscount(totals.getDiscount());
        order.setTax(totals.getTax());
        order.setShippingFee(totals.getShippingFee());
        order.setTotalAmount(totals.getTotalAmount());
        order.setPaymentMethod(paymentMethod.toUpperCase());
        order.setPaymentStatus("COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "COMPLETED");
        order.setOrderStatus("PLACED");
        order.setTrackingNumber(trackingNumber);

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Create Order
            orderDAO.createOrder(order, conn);

            // 2. Create Order Items & Deduct Stock
            for (CartItem ci : cartItems) {
                OrderItem oi = new OrderItem();
                oi.setOrderId(order.getId());
                oi.setProductId(ci.getProductId());
                oi.setSellerId(ci.getProduct().getSellerId());
                oi.setQuantity(ci.getQuantity());
                oi.setUnitPrice(ci.getProduct().getEffectivePrice());
                oi.setTotalPrice(ci.getItemTotal());
                oi.setFulfillmentStatus("CONFIRMED");

                orderDAO.createOrderItem(oi, conn);

                boolean deducted = productDAO.updateStockInTransaction(ci.getProductId(), -ci.getQuantity(), conn);
                if (!deducted) {
                    throw new SQLException("Failed to reserve stock for product ID: " + ci.getProductId());
                }
            }

            // 3. Record Payment
            Payment payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setTransactionRef("TXN-" + paymentMethod.toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 8));
            payment.setPaymentMethod(paymentMethod.toUpperCase());
            payment.setAmount(totals.getTotalAmount());
            payment.setStatus("COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "SUCCESS");
            payment.setGatewayResponse("Mock payment processed successfully for " + paymentMethod);
            paymentDAO.create(payment);

            // 4. Clear Cart
            cartDAO.clearCart(userId, null);

            conn.commit();
            return orderDAO.findById(order.getId());
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public Order placeDirectBuyNowOrder(Long userId, Long addressId, Long productId, int quantity, 
                                        String paymentMethod, String couponCode) throws Exception {
        Product p = productDAO.findById(productId);
        if (p == null || !Boolean.TRUE.equals(p.getIsActive())) {
            throw new IllegalStateException("Product is unavailable.");
        }
        if (p.getStockQuantity() < quantity) {
            throw new IllegalStateException("Only " + p.getStockQuantity() + " units available.");
        }

        BigDecimal subtotal = p.getEffectivePrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = BigDecimal.ZERO;
        if ("HARI10".equalsIgnoreCase(couponCode)) {
            discount = subtotal.multiply(BigDecimal.valueOf(0.10)).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        BigDecimal discountedSubtotal = subtotal.subtract(discount);
        BigDecimal tax = discountedSubtotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal shippingFee = subtotal.compareTo(BigDecimal.valueOf(499.00)) < 0 ? BigDecimal.valueOf(49.00) : BigDecimal.ZERO;
        BigDecimal total = discountedSubtotal.add(tax).add(shippingFee);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTax(tax);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(total);
        order.setPaymentMethod(paymentMethod.toUpperCase());
        order.setPaymentStatus("COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "COMPLETED");
        order.setOrderStatus("PLACED");
        order.setTrackingNumber("HMTRK" + System.currentTimeMillis());

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            orderDAO.createOrder(order, conn);

            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setProductId(productId);
            oi.setSellerId(p.getSellerId());
            oi.setQuantity(quantity);
            oi.setUnitPrice(p.getEffectivePrice());
            oi.setTotalPrice(subtotal);
            oi.setFulfillmentStatus("CONFIRMED");
            orderDAO.createOrderItem(oi, conn);

            productDAO.updateStockInTransaction(productId, -quantity, conn);

            Payment payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setTransactionRef("TXN-" + paymentMethod.toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 8));
            payment.setPaymentMethod(paymentMethod.toUpperCase());
            payment.setAmount(total);
            payment.setStatus("COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "SUCCESS");
            paymentDAO.create(payment);

            conn.commit();
            return orderDAO.findById(order.getId());
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public Order getOrderById(Long id) {
        return orderDAO.findById(id);
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderDAO.findByOrderNumber(orderNumber);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderDAO.findByUserId(userId);
    }

    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = orderDAO.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        if (!order.canCancel()) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Restore inventory stock for all items
            for (OrderItem item : order.getItems()) {
                productDAO.updateStockInTransaction(item.getProductId(), item.getQuantity(), conn);
            }

            orderDAO.updateOrderStatus(orderId, "CANCELLED");
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean updateOrderStatus(Long orderId, String status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }

    private String generateOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randSuffix = String.format("%04d", (int)(Math.random() * 10000));
        return "HM-" + datePrefix + "-" + randSuffix;
    }
}
