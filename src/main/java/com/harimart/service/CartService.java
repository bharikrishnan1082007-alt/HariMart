package com.harimart.service;

import com.harimart.dao.CartDAO;
import com.harimart.dao.ProductDAO;
import com.harimart.model.CartItem;
import com.harimart.model.Product;
import com.harimart.util.CurrencyUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CartService {

    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public List<CartItem> getCartItems(Long userId, String sessionId) {
        return cartDAO.findItemsByUserOrSession(userId, sessionId);
    }

    public CartItem addToCart(Long userId, String sessionId, Long productId, int quantity) throws IllegalArgumentException {
        if (productId == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid product or quantity.");
        }
        Product product = productDAO.findById(productId);
        if (product == null || !Boolean.TRUE.equals(product.getIsActive())) {
            throw new IllegalArgumentException("Product is unavailable.");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + product.getStockQuantity() + " left).");
        }
        return cartDAO.addItem(userId, sessionId, productId, quantity);
    }

    public boolean updateQuantity(Long cartItemId, int quantity) throws IllegalArgumentException {
        if (cartItemId == null) return false;
        if (quantity > 0) {
            CartItem item = cartDAO.findById(cartItemId);
            if (item != null && item.getProduct() != null) {
                if (item.getProduct().getStockQuantity() < quantity) {
                    throw new IllegalArgumentException("Only " + item.getProduct().getStockQuantity() + " units available.");
                }
            }
        }
        return cartDAO.updateQuantity(cartItemId, quantity);
    }

    public boolean removeFromCart(Long cartItemId) {
        return cartDAO.removeItem(cartItemId);
    }

    public boolean clearCart(Long userId, String sessionId) {
        return cartDAO.clearCart(userId, sessionId);
    }

    public void migrateGuestCart(String sessionId, Long userId) {
        cartDAO.migrateGuestCart(sessionId, userId);
    }

    public int getCartCount(Long userId, String sessionId) {
        return cartDAO.getCartCount(userId, sessionId);
    }

    public CartTotals calculateTotals(List<CartItem> items, String couponCode) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getProduct() != null) {
                subtotal = subtotal.add(item.getItemTotal());
            }
        }

        BigDecimal discount = BigDecimal.ZERO;
        boolean couponApplied = false;
        if ("HARI10".equalsIgnoreCase(couponCode) && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            discount = subtotal.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
            couponApplied = true;
        }

        BigDecimal discountedSubtotal = subtotal.subtract(discount);
        if (discountedSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            discountedSubtotal = BigDecimal.ZERO;
        }

        BigDecimal tax = discountedSubtotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal shippingFee = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
            if (subtotal.compareTo(BigDecimal.valueOf(499.00)) < 0) {
                shippingFee = BigDecimal.valueOf(49.00);
            }
        }

        BigDecimal total = discountedSubtotal.add(tax).add(shippingFee);

        return new CartTotals(subtotal, discount, tax, shippingFee, total, couponCode, couponApplied);
    }

    public static class CartTotals {
        private final BigDecimal subtotal;
        private final BigDecimal discount;
        private final BigDecimal tax;
        private final BigDecimal shippingFee;
        private final BigDecimal totalAmount;
        private final String couponCode;
        private final boolean couponApplied;

        public CartTotals(BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal shippingFee, 
                          BigDecimal totalAmount, String couponCode, boolean couponApplied) {
            this.subtotal = subtotal;
            this.discount = discount;
            this.tax = tax;
            this.shippingFee = shippingFee;
            this.totalAmount = totalAmount;
            this.couponCode = couponCode;
            this.couponApplied = couponApplied;
        }

        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDiscount() { return discount; }
        public BigDecimal getTax() { return tax; }
        public BigDecimal getShippingFee() { return shippingFee; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public String getCouponCode() { return couponCode; }
        public boolean isCouponApplied() { return couponApplied; }

        public String getFormattedSubtotal() { return CurrencyUtil.formatINR(subtotal); }
        public String getFormattedDiscount() { return CurrencyUtil.formatINR(discount); }
        public String getFormattedTax() { return CurrencyUtil.formatINR(tax); }
        public String getFormattedShipping() {
            if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) == 0) return "FREE";
            return CurrencyUtil.formatINR(shippingFee);
        }
        public String getFormattedTotal() { return CurrencyUtil.formatINR(totalAmount); }
    }
}
