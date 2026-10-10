package com.harimart.model;

import com.harimart.util.CurrencyUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String brand;
    private String imageUrl;
    private String additionalImages;
    private Boolean isFeatured;
    private Boolean isActive;
    private BigDecimal rating;
    private Integer reviewCount;
    private Timestamp createdAt;

    // Joined fields
    private String sellerStoreName;
    private String categoryName;

    public Product() {
        this.discountPrice = BigDecimal.ZERO;
        this.isFeatured = false;
        this.isActive = true;
        this.rating = BigDecimal.valueOf(4.5);
        this.reviewCount = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAdditionalImages() { return additionalImages; }
    public void setAdditionalImages(String additionalImages) { this.additionalImages = additionalImages; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getSellerStoreName() { return sellerStoreName; }
    public void setSellerStoreName(String sellerStoreName) { this.sellerStoreName = sellerStoreName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    // Helper methods for presentation
    public BigDecimal getEffectivePrice() {
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0 && discountPrice.compareTo(price) < 0) {
            return discountPrice;
        }
        return price != null ? price : BigDecimal.ZERO;
    }

    public boolean hasDiscount() {
        return discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0 && discountPrice.compareTo(price) < 0;
    }

    public int getDiscountPercentage() {
        if (!hasDiscount() || price == null || price.compareTo(BigDecimal.ZERO) == 0) return 0;
        BigDecimal diff = price.subtract(discountPrice);
        return diff.multiply(BigDecimal.valueOf(100)).divide(price, 0, java.math.RoundingMode.HALF_UP).intValue();
    }

    public String getFormattedPrice() {
        return CurrencyUtil.formatINR(getEffectivePrice());
    }

    public String getFormattedOriginalPrice() {
        return CurrencyUtil.formatINR(price);
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
}
