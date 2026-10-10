package com.harimart.model;

import com.harimart.util.CurrencyUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long sellerId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String fulfillmentStatus; // 'CONFIRMED', 'PACKED', 'SHIPPED', 'DELIVERED', 'CANCELLED'
    private Timestamp createdAt;

    // Joined fields
    private String productName;
    private String productImageUrl;
    private String sellerStoreName;
    private String orderNumber;

    public OrderItem() {
        this.fulfillmentStatus = "CONFIRMED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(String fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getSellerStoreName() { return sellerStoreName; }
    public void setSellerStoreName(String sellerStoreName) { this.sellerStoreName = sellerStoreName; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getFormattedUnitPrice() {
        return CurrencyUtil.formatINR(unitPrice);
    }

    public String getFormattedTotalPrice() {
        return CurrencyUtil.formatINR(totalPrice);
    }
}
