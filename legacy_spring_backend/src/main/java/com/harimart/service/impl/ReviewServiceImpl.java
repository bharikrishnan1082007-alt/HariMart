package com.harimart.service.impl;

import com.harimart.dto.ReviewDTO;
import com.harimart.dto.ReviewRequest;
import com.harimart.entity.Product;
import com.harimart.entity.Review;
import com.harimart.entity.User;
import com.harimart.enums.Role;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.exception.UnauthorizedException;
import com.harimart.repository.ProductRepository;
import com.harimart.repository.ReviewRepository;
import com.harimart.service.AuthService;
import com.harimart.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository,
                             AuthService authService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @Override
    public List<ReviewDTO> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO addReview(Long productId, ReviewRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment().trim() : "");

        Review saved = reviewRepository.save(review);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User user = authService.getCurrentAuthenticatedUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (user.getRole() != Role.ROLE_ADMIN && !review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO mapToDTO(Review r) {
        return new ReviewDTO(
                r.getId(),
                r.getProduct().getId(),
                r.getProduct().getName(),
                r.getUser().getId(),
                r.getUser().getName(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
