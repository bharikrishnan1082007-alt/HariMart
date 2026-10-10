package com.harimart.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    private static final Locale INDIA_LOCALE = new Locale("en", "IN");

    public static String formatINR(BigDecimal amount) {
        if (amount == null) {
            return "\u20B90.00";
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(INDIA_LOCALE);
        String formatted = formatter.format(amount);
        // Clean symbol to ensure standard ₹ symbol representation
        return formatted.replace("INR", "\u20B9").replace("Rs.", "\u20B9").trim();
    }

    public static String formatINR(double amount) {
        return formatINR(BigDecimal.valueOf(amount));
    }
}
