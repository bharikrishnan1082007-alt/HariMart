package com.harimart.service.impl;

import com.harimart.dto.AddToCartRequest;
import com.harimart.dto.CartDTO;
import com.harimart.dto.CartItemDTO;
import com.harimart.dto.UpdateCartRequest;
import com.harimart.entity.Cart;
import com.harimart.entity.CartItem;
import com.harimart.entity.Product;
import com.harimart.entity.User;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.repository.CartItemRepository;
import com.harimart.repository.CartRepository;
import com.harimart.repository.ProductRepository;
import com.harimart.service.AuthService;
import com.harimart.service.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    @Value("${app.promo.code:HARI10}")
    private String promoCode;

    @Value("${app.promo.discount-percentage:10}")
    private int discountPercentage;

    @Value("${app.shipping.free-threshold:499.00}")
    private BigDecimal freeShippingThreshold;

    @Value("${app.shipping.flat-fee:49.00}")
    private BigDecimal flatShippingFee;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           AuthService authService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    private Cart getOrCreateUserCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    @Override
    public CartDTO getCart(String couponCode) {
        User user = authService.getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);
        return buildCartDTO(cart, couponCode);
    }

    @Override
    @Transactional
    public CartDTO addToCart(AddToCartRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new BadRequestException("This product is currently unavailable");
        }

        if (product.getStock() <= 0) {
            throw new BadRequestException("Product is out of stock");
        }

        int requestedQuantity = request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1;

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + requestedQuantity;
            if (newQuantity > product.getStock()) {
                throw new BadRequestException("Cannot add more. Only " + product.getStock() + " units available in stock");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setPrice(product.getFinalPrice());
            cartItemRepository.save(existingItem);
        } else {
            if (requestedQuantity > product.getStock()) {
                throw new BadRequestException("Cannot add " + requestedQuantity + " units. Only " + product.getStock() + " available");
            }
            CartItem newItem = new CartItem(null, cart, product, requestedQuantity, product.getFinalPrice());
            cartItemRepository.save(newItem);
        }

        return buildCartDTO(cart, null);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long cartItemId, UpdateCartRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to your cart");
        }

        Product product = item.getProduct();
        if (request.getQuantity() > product.getStock()) {
            throw new BadRequestException("Requested quantity (" + request.getQuantity() + ") exceeds available stock (" + product.getStock() + ")");
        }

        item.setQuantity(request.getQuantity());
        item.setPrice(product.getFinalPrice());
        cartItemRepository.save(item);

        return buildCartDTO(cart, null);
    }

    @Override
    @Transactional
    public CartDTO removeCartItem(Long cartItemId) {
        User user = authService.getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to your cart");
        }

        cartItemRepository.delete(item);
        return buildCartDTO(cart, null);
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = authService.getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public CartDTO buildCartDTO(Cart cart, String couponCode) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemDTO> itemDTOs = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : items) {
            Product p = item.getProduct();
            BigDecimal unitPrice = p.getFinalPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            subtotal = subtotal.add(itemTotal);
            totalQuantity += item.getQuantity();

            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(p.getId());
            itemDTO.setProductName(p.getName());
            itemDTO.setProductImage(p.getImageUrl());
            itemDTO.setBrand(p.getBrand());
            itemDTO.setOriginalPrice(p.getPrice());
            itemDTO.setDiscount(p.getDiscount());
            itemDTO.setUnitPrice(unitPrice);
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setStock(p.getStock());
            itemDTO.setItemTotal(itemTotal);

            itemDTOs.add(itemDTO);
        }

        // Coupon calculation
        BigDecimal discount = BigDecimal.ZERO;
        String appliedCoupon = null;
        if (couponCode != null && promoCode.equalsIgnoreCase(couponCode.trim()) && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            appliedCoupon = promoCode;
            discount = subtotal.multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // Shipping calculation
        BigDecimal deliveryCharge = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
            if (subtotal.compareTo(freeShippingThreshold) < 0) {
                deliveryCharge = flatShippingFee;
            }
        }

        BigDecimal finalTotal = subtotal.subtract(discount).add(deliveryCharge).max(BigDecimal.ZERO);

        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setItems(itemDTOs);
        cartDTO.setSubtotal(subtotal);
        cartDTO.setDiscount(discount);
        cartDTO.setCouponCode(appliedCoupon);
        cartDTO.setDeliveryCharge(deliveryCharge);
        cartDTO.setFinalTotal(finalTotal);
        cartDTO.setTotalItems(totalQuantity);

        return cartDTO;
    }
}
