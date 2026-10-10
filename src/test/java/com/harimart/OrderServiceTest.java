package com.harimart;

import com.harimart.model.CartItem;
import com.harimart.model.Product;
import com.harimart.service.CartService;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderServiceTest {

    @Test
    public void testCartTotalsCalculationWithFreeShipping() {
        CartService cartService = new CartService();
        List<CartItem> items = new ArrayList<>();

        Product p1 = new Product();
        p1.setPrice(new BigDecimal("1000.00"));
        p1.setDiscountPrice(BigDecimal.ZERO);

        CartItem item1 = new CartItem(1L, 1L, null, 1L, 1, p1);
        items.add(item1);

        // Subtotal = 1000.00 >= 499 => Shipping = FREE
        CartService.CartTotals totals = cartService.calculateTotals(items, null);
        assertEquals(0, new BigDecimal("1000.00").compareTo(totals.getSubtotal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(totals.getShippingFee()));
        assertEquals("FREE", totals.getFormattedShipping());
        // Tax is 5% of 1000 = 50.00
        assertEquals(0, new BigDecimal("50.00").compareTo(totals.getTax()));
        // Total = 1000 + 50 = 1050.00
        assertEquals(0, new BigDecimal("1050.00").compareTo(totals.getTotalAmount()));
        assertFalse(totals.isCouponApplied());
    }

    @Test
    public void testCartTotalsWithCouponHARI10AndUnderThresholdShipping() {
        CartService cartService = new CartService();
        List<CartItem> items = new ArrayList<>();

        Product p1 = new Product();
        p1.setPrice(new BigDecimal("200.00"));
        p1.setDiscountPrice(BigDecimal.ZERO);

        CartItem item1 = new CartItem(1L, 1L, null, 1L, 1, p1);
        items.add(item1);

        // Subtotal = 200.00 < 499 => Shipping = 49.00
        // Coupon HARI10 = 10% discount = 20.00
        // Discounted subtotal = 180.00
        // Tax 5% of 180 = 9.00
        // Total = 180 + 9 + 49 = 238.00
        CartService.CartTotals totals = cartService.calculateTotals(items, "HARI10");
        assertTrue(totals.isCouponApplied());
        assertEquals(0, new BigDecimal("20.00").compareTo(totals.getDiscount()));
        assertEquals(0, new BigDecimal("49.00").compareTo(totals.getShippingFee()));
        assertEquals(0, new BigDecimal("9.00").compareTo(totals.getTax()));
        assertEquals(0, new BigDecimal("238.00").compareTo(totals.getTotalAmount()));
    }
}
