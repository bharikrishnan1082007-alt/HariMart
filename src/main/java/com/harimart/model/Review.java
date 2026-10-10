package com.harimart.model;

import java.sql.Timestamp;

public class Review {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String title;
    private String comment;
    private Boolean isVerifiedPurchase;
    private Timestamp createdAt;

    // Joined fields
    private String userName;
    private String productName;

    public Review() {
        this.isVerifiedPurchase = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getIsVerifiedPurchase() { return isVerifiedPurchase; }
    public void setIsVerifiedPurchase(Boolean isVerifiedPurchase) { this.isVerifiedPurchase = isVerifiedPurchase; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
