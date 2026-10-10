package com.harimart.controller;

import com.harimart.dto.AddToCartRequest;
import com.harimart.dto.ApiResponse;
import com.harimart.dto.CartDTO;
import com.harimart.dto.UpdateCartRequest;
import com.harimart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestParam(required = false) String coupon) {
        CartDTO cart = cartService.getCart(coupon);
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartDTO cart = cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartRequest request) {
        CartDTO cart = cartService.updateCartItem(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cart));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> removeCartItem(@PathVariable Long id) {
        CartDTO cart = cartService.removeCartItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}
