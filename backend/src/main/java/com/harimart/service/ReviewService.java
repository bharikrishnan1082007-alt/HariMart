package com.harimart.service;

import com.harimart.dto.ReviewDTO;
import com.harimart.dto.ReviewRequest;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getProductReviews(Long productId);

    ReviewDTO addReview(Long productId, ReviewRequest request);

    void deleteReview(Long reviewId);

    List<ReviewDTO> getAllReviews();
}
