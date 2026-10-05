package com.harimart.service;

import com.harimart.dto.ProductDTO;
import java.util.List;

public interface WishlistService {

    List<ProductDTO> getWishlist();

    void addToWishlist(Long productId);

    void removeFromWishlist(Long productId);

    boolean isInWishlist(Long productId);
}
