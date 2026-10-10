package com.harimart.model;

import com.harimart.util.CurrencyUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CartItem {
    private Long id;
    private Long userId;
    private String sessionId;
    private Long productId;
    private Integer quantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined Product Details
    private Product product;

    public CartItem() {
        this.quantity = 1;
    }

    public CartItem(Long id, Long userId, String sessionId, Long productId, Integer quantity, Product product) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.productId = productId;
        this.quantity = quantity != null ? quantity : 1;
        this.product = product;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getItemTotal() {
        if (product == null) return BigDecimal.ZERO;
        return product.getEffectivePrice().multiply(BigDecimal.valueOf(quantity));
    }

    public String getFormattedItemTotal() {
        return CurrencyUtil.formatINR(getItemTotal());
    }
}
