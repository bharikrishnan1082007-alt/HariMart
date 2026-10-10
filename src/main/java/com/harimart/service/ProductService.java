package com.harimart.service;

import com.harimart.dao.CategoryDAO;
import com.harimart.dao.ProductDAO;
import com.harimart.dao.ReviewDAO;
import com.harimart.model.Category;
import com.harimart.model.Product;
import com.harimart.model.Review;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public Product getProductById(Long id) {
        return productDAO.findById(id);
    }

    public Product getProductBySlug(String slug) {
        return productDAO.findBySlug(slug);
    }

    public List<Product> getFeaturedProducts(int limit) {
        return productDAO.findFeatured(limit);
    }

    public List<Product> getNewArrivals(int limit) {
        return productDAO.findNewArrivals(limit);
    }

    public List<Product> getBestsellers(int limit) {
        return productDAO.findBestsellers(limit);
    }

    public List<Product> getRelatedProducts(Long productId, Long categoryId, int limit) {
        return productDAO.findRelated(productId, categoryId, limit);
    }

    public List<Category> getActiveCategories() {
        return categoryDAO.findAllActive();
    }

    public Category getCategoryBySlug(String slug) {
        return categoryDAO.findBySlug(slug);
    }

    public Category getCategoryById(Long id) {
        return categoryDAO.findById(id);
    }

    public List<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                       Double minRating, Boolean inStock, String sort, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return productDAO.search(keyword, categoryId, minPrice, maxPrice, minRating, inStock, sort, offset, pageSize);
    }

    public int countSearchResults(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                 Double minRating, Boolean inStock) {
        return productDAO.countSearch(keyword, categoryId, minPrice, maxPrice, minRating, inStock);
    }

    public List<String> autocomplete(String keyword, int limit) {
        if (keyword == null || keyword.trim().length() < 2) return List.of();
        return productDAO.autocomplete(keyword, limit);
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewDAO.findByProductId(productId);
    }

    public boolean addReview(Long productId, Long userId, int rating, String title, String comment) {
        if (productId == null || userId == null || rating < 1 || rating > 5) return false;
        if (reviewDAO.hasReviewed(userId, productId)) return false;

        boolean isVerified = reviewDAO.hasPurchased(userId, productId);

        Review r = new Review();
        r.setProductId(productId);
        r.setUserId(userId);
        r.setRating(rating);
        r.setTitle(title != null ? title.trim() : "");
        r.setComment(comment != null ? comment.trim() : "");
        r.setIsVerifiedPurchase(isVerified);

        return reviewDAO.create(r);
    }
}
