package com.harimart;

import com.harimart.model.Product;
import com.harimart.util.CurrencyUtil;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CurrencyAndProductTest {

    @Test
    public void testCurrencyFormatting() {
        String formatted = CurrencyUtil.formatINR(BigDecimal.valueOf(1499.00));
        assertNotNull(formatted);
        assertTrue(formatted.contains("1,499") || formatted.contains("1499"));

        String zero = CurrencyUtil.formatINR(BigDecimal.ZERO);
        assertNotNull(zero);
        assertTrue(zero.contains("0"));
    }

    @Test
    public void testProductDiscountCalculations() {
        Product p = new Product();
        p.setPrice(new BigDecimal("2000.00"));
        p.setDiscountPrice(new BigDecimal("1500.00"));
        p.setStockQuantity(15);

        assertTrue(p.hasDiscount());
        assertEquals(25, p.getDiscountPercentage());
        assertEquals(new BigDecimal("1500.00"), p.getEffectivePrice());
        assertTrue(p.isInStock());

        p.setStockQuantity(0);
        assertFalse(p.isInStock());
    }
}
