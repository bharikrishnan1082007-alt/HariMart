package com.harimart.controller;

import com.harimart.dto.ApiResponse;
import com.harimart.dto.ReviewDTO;
import com.harimart.dto.ReviewRequest;
import com.harimart.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getProductReviews(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewDTO>> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewDTO review = reviewService.addReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Review submitted successfully", review));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.ok("Review deleted successfully", null));
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }
}
