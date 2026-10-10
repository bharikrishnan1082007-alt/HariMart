package com.harimart.service;

import com.harimart.dto.AddToCartRequest;
import com.harimart.dto.CartDTO;
import com.harimart.dto.UpdateCartRequest;

public interface CartService {

    CartDTO getCart(String couponCode);

    CartDTO addToCart(AddToCartRequest request);

    CartDTO updateCartItem(Long cartItemId, UpdateCartRequest request);

    CartDTO removeCartItem(Long cartItemId);

    void clearCart();
}
