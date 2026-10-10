package com.harimart.service.impl;

import com.harimart.dto.ProductDTO;
import com.harimart.entity.Product;
import com.harimart.entity.User;
import com.harimart.entity.Wishlist;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.repository.ProductRepository;
import com.harimart.repository.WishlistRepository;
import com.harimart.service.AuthService;
import com.harimart.service.ProductService;
import com.harimart.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final AuthService authService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               ProductRepository productRepository,
                               ProductService productService,
                               AuthService authService) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.authService = authService;
    }

    @Override
    public List<ProductDTO> getWishlist() {
        User user = authService.getCurrentAuthenticatedUser();
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        return wishlists.stream()
                .map(w -> productService.getProductById(w.getProduct().getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToWishlist(Long productId) {
        User user = authService.getCurrentAuthenticatedUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            Wishlist wishlist = new Wishlist(user, product);
            wishlistRepository.save(wishlist);
        }
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = authService.getCurrentAuthenticatedUser();
        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    @Override
    public boolean isInWishlist(Long productId) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
        } catch (Exception e) {
            return false;
        }
    }
}
