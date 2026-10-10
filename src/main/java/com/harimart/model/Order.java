package com.harimart.model;

import com.harimart.util.CurrencyUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private String orderNumber;
    private Long userId;
    private Long addressId;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private String paymentMethod; // 'COD', 'UPI', 'CARD'
    private String paymentStatus; // 'PENDING', 'COMPLETED', 'FAILED'
    private String orderStatus; // 'PLACED', 'CONFIRMED', 'PACKED', 'SHIPPED', 'DELIVERED', 'CANCELLED'
    private String trackingNumber;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined / Association
    private Address shippingAddress;
    private User buyer;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.subtotal = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.shippingFee = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = "PENDING";
        this.orderStatus = "PLACED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getFormattedTotal() {
        return CurrencyUtil.formatINR(totalAmount);
    }

    public String getFormattedSubtotal() {
        return CurrencyUtil.formatINR(subtotal);
    }

    public String getFormattedDiscount() {
        return CurrencyUtil.formatINR(discount);
    }

    public String getFormattedShipping() {
        if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) == 0) {
            return "FREE";
        }
        return CurrencyUtil.formatINR(shippingFee);
    }

    public String getFormattedTax() {
        return CurrencyUtil.formatINR(tax);
    }

    public boolean canCancel() {
        return "PLACED".equalsIgnoreCase(orderStatus) || "CONFIRMED".equalsIgnoreCase(orderStatus);
    }
}
